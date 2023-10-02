from django.contrib.auth import get_user_model, authenticate
from rest_framework import serializers
from rest_framework.exceptions import ValidationError
from rest_framework_simplejwt.serializers import TokenObtainPairSerializer

User = get_user_model()


class UserSignUpSerializer(serializers.Serializer):
    email = serializers.EmailField(required=True, max_length=50)
    password = serializers.CharField(required=True)
    nickname = serializers.CharField(required=True, max_length=15)

    def validate(self, data):
        pw = data.get('password')
        if len(pw) < 8:
            raise ValidationError("password unavailable (short password)")

        email = data.get('email')

        if User.objects.filter(email=email).exists():
            raise ValidationError('email address unavailable (already taken)')
        return data

    def create(self, validated_data):
        email = validated_data.get('email')
        password = validated_data.get('password')
        nickname = validated_data.get('nickname')
        user = User.objects.create_user(nickname, email, password)
        return user


class UserLoginSerializer(serializers.Serializer):
    email = serializers.EmailField(required=True)
    password = serializers.CharField(write_only=True)
    token = serializers.CharField(max_length=255, read_only=True)

    def validate(self, data):
        email = data.get('email', None)
        if not User.objects.filter(email=email).exists():
            raise ValidationError('wrong email address')

        password = data.get('password', None)
        user = authenticate(email=email, password=password)
        if user is None:
            raise ValidationError("wrong password")

        token = TokenObtainPairSerializer.get_token(user)
        refresh = str(token)
        access = str(token.access_token)
        jwt_token = {
            'access': access,
            'refresh': refresh
        }

        return {
            'token': jwt_token
        }


class UserLogoutSerializer(serializers.Serializer):
    refresh = serializers.CharField(required=True)