from django.contrib.auth import get_user_model
from rest_framework import serializers
from rest_framework.exceptions import ValidationError

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