from django.test import TestCase
from rest_framework import status

from user.models import User, EmailVerification


class UserTest(TestCase):

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

    def test_logout_success(self):
        headers = {
            'HTTP_AUTHORIZATION': f'Bearer {self.access_token}'
        }
        data = {
            'refresh': self.refresh_token
        }
        response = self.client.post('/user/logout/', data, **headers)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_refresh_success(self):
        data = {
            'refresh': self.refresh_token
        }
        response = self.client.post('/user/refresh/', data)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_withdraw_success(self):
        headers = {
            'HTTP_AUTHORIZATION': f'Bearer {self.access_token}'
        }
        data = {
            'refresh': self.refresh_token
        }
        response = self.client.post('/user/withdraw/', data, **headers)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_change_nickname_success(self):
        headers = {
            'HTTP_AUTHORIZATION': f'Bearer {self.access_token}'
        }
        data = {
            'nickname': 'changed_name'
        }
        response = self.client.patch('/user/profile/nickname/', data, content_type='application/json', **headers)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        # Call get-profile API to check whether the value change is applied
        response = self.client.get('/user/profile/', **headers)
        self.assertEqual(data['nickname'], response.json()['user']['nickname'])

    def test_change_password_success(self):
        headers = {
            'HTTP_AUTHORIZATION': f'Bearer {self.access_token}'
        }
        data = {
            'password': 'changed_pw'
        }
        self.client.patch('/user/profile/password/', data, content_type='application/json', **headers)

        new_data = {
            'email': 'test_email@gmail.com',
            'password': data['password'],
        }
        response = self.client.post('/user/login/', new_data)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_email_verification_signup_success(self):
        data = {
            'email': 'haha@gmail.com'
        }
        # Sending
        response = self.client.post('/user/validateemail/signup/send/', data)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        # Accepting
        email_veri = EmailVerification.objects.get(email=data['email'])
        verify_data = {
            'email': 'haha@gmail.com',
            'code': email_veri.code
        }
        response = self.client.post('/user/validateemail/signup/accept/', verify_data)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_email_verification_pw_success(self):
        data = {
            'email': 'test_email@gmail.com'
        }
        # Sending
        response = self.client.post('/user/validateemail/pw/send/', data)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        # Accepting
        email_veri = EmailVerification.objects.get(email=data['email'])
        verify_data = {
            'email': 'test_email@gmail.com',
            'code': email_veri.code
        }
        response = self.client.post('/user/validateemail/pw/accept/', verify_data)
        self.assertEqual(response.status_code, status.HTTP_200_OK)




