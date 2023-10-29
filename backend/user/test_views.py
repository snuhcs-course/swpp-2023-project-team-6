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

    def test_signup_success(self):
        data = {
            'email': 'this_is_test@gmail.com',
            'password': 'this_is_test',
            'nickname': 'this_is_test'
        }
        response = self.client.post('/user/signup/', data)
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
