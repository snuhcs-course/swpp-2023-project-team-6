from django.contrib.auth import get_user_model, authenticate
from rest_framework import serializers
from rest_framework.exceptions import ValidationError
from rest_framework_simplejwt.serializers import TokenObtainPairSerializer

from user.models import EmailVerification
from user.utils import normalize_email

User = get_user_model()


class UserSignUpSerializer(serializers.Serializer):
    email = serializers.EmailField(required=True, max_length=50)
    password = serializers.CharField(required=True)
    nickname = serializers.CharField(required=True, max_length=15)

    def validate(self, data):
        pw = data.get('password')
        if len(pw) < 8:
            raise ValidationError({"short": ["password unavailable (short password)"]})

        email = data.get('email')

        if User.objects.filter(email=email).exists():
            raise ValidationError({"already_taken": ["email address unavailable (already taken)"]})
        return data

    def create(self, validated_data):
        email = validated_data.get('email')
        password = validated_data.get('password')
        nickname = validated_data.get('nickname')
        user = User.objects.create_user(nickname, email, password)
        return user


class EmailCheckSerializer(serializers.Serializer):
    email = serializers.EmailField(required=True, max_length=50)

    def validate(self, data):
        email = normalize_email(data.get('email'))
        if User.objects.filter(email=email).exists():
            raise ValidationError({"already_taken": ["email address unavailable (already taken)"]})

        return data


# Checks whether any user exists with a particular email
class UserCheckSerializer(serializers.Serializer):
    email = serializers.EmailField(required=True, max_length=50)

    def validate(self, data):
        email = normalize_email(data.get('email'))
        if not User.objects.filter(email=email).exists():
            raise ValidationError({"no_user": ["no such user w/ the email"]})

        return data


class CodeCheckSerializer(serializers.Serializer):
    email = serializers.EmailField(required=True, max_length=50)
    code = serializers.CharField(required=True, max_length=6)

    def validate(self, data):
        email = normalize_email(data.get('email'))
        code = data.get('code')
        if not len(code) == 6:
            raise ValidationError({"code": ["Invalid code length(should be 6)"]})

        if not EmailVerification.objects.filter(email=email, code=code).exists():
            raise ValidationError({"fail": ["wrong code"]})
        else:
            # successfully verified
            verification = EmailVerification.objects.get(email=email, code=code)
            verification.delete()
        return data


class UserLoginSerializer(serializers.Serializer):
    email = serializers.EmailField(required=True)
    password = serializers.CharField(write_only=True)
    token = serializers.CharField(max_length=255, read_only=True)

    def validate(self, data):
        email = data.get('email', None)
        if not User.objects.filter(email=email).exists():
            raise ValidationError({"wrong_email": ["wrong email address"]})

        password = data.get('password', None)
        user = authenticate(email=email, password=password)
        if user is None:
            raise ValidationError({"wrong_password": ["wrong password"]})

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


class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = (
            'id',
            'email',
            'nickname',
            'updated_at',
        )


class PasswordUpdateSerializer(serializers.Serializer):
    password = serializers.CharField(required=True)

    def validate(self, data):
        password = data.get('password')
        if len(password) < 8:
            raise ValidationError({"short": ["password unavailable (short password)"]})
        return data

    def update(self, user, validated_data):
        password = validated_data.get('password')
        user.set_password(password)
        user.save()
        return user


class NicknameUpdateSerializer(serializers.Serializer):
    nickname = serializers.CharField(required=True, max_length=15)

    def validate(self, data):
        user = self.context['user']
        nickname = data.get('nickname')
        if user.nickname == nickname:
            raise ValidationError({"same": ["The new nickname is the same as the original one"]})
        return data

    def update(self, user, validated_data):
        nickname = validated_data.get('nickname')
        user.nickname = nickname
        user.save()
        return user
