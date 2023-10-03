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


class UserProfileUpdateSerializer(serializers.Serializer):
    email = serializers.EmailField(required=False, max_length=50)
    nickname = serializers.CharField(required=False, max_length=15)
    origin_password = serializers.CharField(required=True)
    new_password = serializers.CharField(required=False)

    def validate(self, data):
        user = self.context['user']
        origin_password = data.get('origin_password')
        origin_email = user.email
        new_password = data.get('new_password')
        email = data.get('email')
        nickname = data.get('nickname')

        if not email and not nickname and not new_password:
            raise ValidationError("At least one of email, nickname, or password must be provided.")

        user = authenticate(email=origin_email, password=origin_password)
        if user is None:
            raise ValidationError("wrong password (original password)")

        if new_password:
            if len(new_password) < 8:
                raise ValidationError("password unavailable (short password)")
        if email:
            normalize_email(email)
            if user.email == email:
                raise ValidationError('새 이메일이 기존 이메일과 같습니다.')
            if User.objects.filter(email=email).exists():
                raise ValidationError('email address unavailable (already taken)')
            data['email'] = email
        if nickname:
            if user.nickname == nickname:
                raise ValidationError('새 닉네임이 기존 닉네임과 같습니다.')

        return data

    def update(self, user, validated_data):
        new_password = validated_data.get('new_password')
        email = validated_data.get('email')
        nickname = validated_data.get('nickname')
        queryset = User.objects.filter(email=email)

        if new_password is not None:
            user.set_password(new_password)

        if email is not None:
            user.email = email

        if nickname is not None:
            user.nickname = nickname

        user.save()
        return user
