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
            raise ValidationError({"password": ["password unavailable (short password)"]})

        email = data.get('email')

        if User.objects.filter(email=email).exists():
            raise ValidationError({"email": ["email address unavailable (already taken)"]})
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
            raise ValidationError({"email": ["wrong email address"]})

        password = data.get('password', None)
        user = authenticate(email=email, password=password)
        if user is None:
            raise ValidationError({"password": ["wrong password"]})

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


def normalize_email(email):
    try:
        email_name, domain_part = email.strip().rsplit("@", 1)
    except ValueError:
        pass
    else:
        email = email_name + "@" + domain_part.lower()
    return email


class EmailCheckSerializer(serializers.Serializer):
    email = serializers.EmailField(required=True, max_length=50)

    def validate(self, data):
        email = normalize_email(data.get('email'))
        if User.objects.filter(email=email).exists():
            raise ValidationError({"email": ["email address unavailable (already taken)"]})

        return data


class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = (
            'id',
            'email',
            'nickname',
            'email_verified',
            'updated_at',
        )


class EmailUpdateSerializer(serializers.Serializer):
    email = serializers.EmailField(required=True, max_length=50)

    def validate(self, data):
        user = self.context['user']
        email = data.get('email')

        if email:
            normalize_email(email)
            if user.email == email:
                raise ValidationError({"email": ["The new email address is the same as the original one"]})
            if User.objects.filter(email=email).exists():
                raise ValidationError({"email": ["email address unavailable (already taken)"]})
            data['email'] = email
        else:
            raise ValidationError({"email": ["The field is empty"]})
        return data

    def update(self, user, validated_data):
        email = validated_data.get('email')
        user.email = email
        user.save()
        return user


class PasswordUpdateSerializer(serializers.Serializer):
    password = serializers.CharField(required=True)

    def validate(self, data):
        password = data.get('password')
        if password:
            if len(password) < 8:
                raise ValidationError({"password": ["short password"]})
        else:
            raise ValidationError({"password": ["The field is empty"]})
        return data

    def update(self, user, validated_data):
        password = validated_data.get('password')
        user.password = password
        user.save()
        return user


class NicknameUpdateSerializer(serializers.Serializer):
    nickname = serializers.CharField(required=True, max_length=15)

    def validate(self, data):
        user = self.context['user']
        nickname = data.get('nickname')
        if nickname:
            if user.nickname == nickname:
                raise ValidationError({"nickname": ["The new nickname is the same as the original one"]})
        else:
            raise ValidationError({"nickname": ["The field is empty"]})
        return data

    def update(self, user, validated_data):
        nickname = validated_data.get('nickname')
        user.nickname = nickname
        user.save()
        return user
