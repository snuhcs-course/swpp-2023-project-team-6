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
        data = response.json()
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(data.get('results')[0], expected_response)