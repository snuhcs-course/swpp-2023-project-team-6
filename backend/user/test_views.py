from django.test import TestCase
from rest_framework import status

from user.models import User


class UserTest(TestCase):

    def setUp(self):
        self.user = User.objects.create_user(
            email='test_email@gmail.com',
            password='test_password',
            nickname='test_nickname'
        )
        self.access_token = self.login_and_get_access_token()

    def login_and_get_access_token(self):
        data = {
            'email': 'test_email@gmail.com',
            'password': 'test_password',
        }
        response = self.client.post('/user/login/', data)
        if response.status_code == status.HTTP_200_OK:
            return response.json()['access']
        return None

    def test_signup_success(self):
        data = {
            'email': 'this_is_test@gmail.com',
            'password': 'this_is_test',
            'nickname': 'this_is_test'
        }
        response = self.client.post('/user/signup/', data)
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)

    def test_login_success(self):
        data = {
            'email': 'test_email@gmail.com',
            'password': 'test_password',
        }
        response = self.client.post('/user/login/', data)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_get_profile_success(self):
        headers = {
            'HTTP_AUTHORIZATION': f'Bearer {self.access_token}'
        }
        response = self.client.get('/user/profile/', **headers)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        data = response.json()['user']
        self.assertEqual(self.user.email, data['email'])
        self.assertEqual(self.user.nickname, data['nickname'])
