from django.core.files.uploadedfile import SimpleUploadedFile
from django.test import TestCase
from rest_framework import status

from entry.models import Symbol
from user.models import User


class SymbolTest(TestCase):

    def setUp(self):
        self.user = User.objects.create_user(
            email='test_email@gmail.com',
            password='test_password',
            nickname='test_nickname'
        )
        self.tokens = self.login_and_get_tokens()
        self.access_token = self.tokens.get('access')
        self.refresh_token = self.tokens.get('refresh')

        Symbol.objects.create(id=501, text="test1", category=1, created_by=self.user)
        Symbol.objects.create(id=502, text="test2", category=2, created_by=self.user)
        Symbol.objects.create(id=503, text="test3", category=3, created_by=self.user)

    def login_and_get_tokens(self):
        data = {
            'email': 'test_email@gmail.com',
            'password': 'test_password',
        }
        response = self.client.post('/user/login/', data)
        if response.status_code == status.HTTP_200_OK:
            tokens = response.json()
            return tokens
        return None

    def test_favorite_backup_success(self):
        headers = {
            'HTTP_AUTHORIZATION': f'Bearer {self.access_token}'
        }
        # Add some favorites to the user's list
        response = self.client.post('/symbol/favorite/backup/?id=501,502,503', **headers)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_get_favorite_symbols_success(self):
        headers = {
            'HTTP_AUTHORIZATION': f'Bearer {self.access_token}'
        }
        expected_response = {
            'id': 501
        }
        # Add favorite symbol to check
        response = self.client.post('/symbol/favorite/backup/?id=501', **headers)

        response = self.client.get('/symbol/favorite/backup/', **headers)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        data = response.json()
        self.assertEqual(data.get('results')[0], expected_response)

    def test_enable_my_symbols_success(self):
        headers = {
            'HTTP_AUTHORIZATION': f'Bearer {self.access_token}'
        }
        # Enable user-created symbols
        response = self.client.post('/symbol/enable/?id=501', **headers)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_enable_my_symbols_fail_no_such_symbol(self):
        headers = {
            'HTTP_AUTHORIZATION': f'Bearer {self.access_token}'
        }
        # Enable user-created symbols
        response = self.client.post('/symbol/enable/?id=505', **headers)
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_get_my_symbols_success(self):
        headers = {
            'HTTP_AUTHORIZATION': f'Bearer {self.access_token}'
        }
        # Get all of the user-created symbols
        self.client.post('/symbol/enable/?id=501', **headers)  # since ONLY enabled symbols can be retrieved
        response = self.client.get('/symbol/', **headers)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        data = response.json()
        self.assertEqual(len(data.get('my_symbols')), 1)  # since only one symbol is enabled

    def test_get_my_specific_symbol_success(self):
        headers = {
            'HTTP_AUTHORIZATION': f'Bearer {self.access_token}'
        }
        # Get all of the user-created symbols
        self.client.post('/symbol/enable/?id=501', **headers)  # since ONLY enabled symbols can be retrieved
        response = self.client.get('/symbol/501/', **headers)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        data = response.json()
        self.assertIsNotNone(data.get('my_symbol'))

    def test_get_my_specific_symbol_fail_invalid_symbol(self):
        headers = {
            'HTTP_AUTHORIZATION': f'Bearer {self.access_token}'
        }
        # Get all of the user-created symbols
        response = self.client.get('/symbol/501/', **headers)
        # Since symbol 501 is not valid yet
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
