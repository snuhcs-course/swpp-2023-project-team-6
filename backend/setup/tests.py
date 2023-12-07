from django.test import TestCase
from rest_framework import status

from user.models import User


class SettingsTest(TestCase):

    def setUp(self):
        self.user = User.objects.create_user(
            email='test_email@gmail.com',
            password='test_password',
            nickname='test_nickname'
        )
        self.tokens = self.login_and_get_tokens()
        self.access_token = self.tokens.get('access')
        self.refresh_token = self.tokens.get('refresh')

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

    def test_settings_backup_success(self):
        headers = {
            'HTTP_AUTHORIZATION': f'Bearer {self.access_token}'
        }
        data = {
            'display_mode': 0,
            'default_menu': 1
        }
        # when attempting backup for the first time
        response = self.client.post('/setting/backup/', data, **headers)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        # when attempting backup repetitively
        data = {
            'display_mode': 1,
            'default_menu': 0
        }
        response = self.client.post('/setting/backup/', data, **headers)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_get_settings_success(self):
        headers = {
            'HTTP_AUTHORIZATION': f'Bearer {self.access_token}'
        }
        response = self.client.get('/setting/backup/', **headers)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        data = response.json()
        self.assertEqual(data['display_mode'], 0)  # 0 is the default value
        self.assertEqual(data['default_menu'], 1)  # 1 is the default value








