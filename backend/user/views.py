import boto3
from django.contrib.auth import get_user_model
from django.core.mail import EmailMessage
from rest_framework import status, permissions
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework_simplejwt.serializers import TokenObtainPairSerializer
from rest_framework_simplejwt.tokens import RefreshToken

from config import settings
from config.utils import AccessToken
from entry.models import Symbol
from .models import EmailVerification

from .serializers import UserSignUpSerializer, UserLoginSerializer, UserLogoutSerializer, EmailCheckSerializer, \
    UserProfileSerializer, PasswordUpdateSerializer, NicknameUpdateSerializer, CodeCheckSerializer, UserCheckSerializer
from .utils import generate_code, message

User = get_user_model()


class UserSignUpView(APIView):
    permission_classes = (permissions.AllowAny,)

    def post(self, request):
        serializer = UserSignUpSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()

        return Response(status=status.HTTP_201_CREATED)


class SignUpEmailVerifySendView(APIView):
    permission_classes = (permissions.AllowAny,)

    def post(self, request):
        serializer = EmailCheckSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        email = serializer.validated_data['email']
        code = generate_code()
        message_data = message(code)

        mail_title = "Speech Buddy 회원가입 인증 메일입니다."
        mail_to = email
        EmailMessage(mail_title, message_data, to=[mail_to]).send()

        if not EmailVerification.objects.filter(email=email).exists():
            EmailVerification.objects.create(email=email, code=code)
        else:
            verification = EmailVerification.objects.get(email=email)
            verification.code = code
            verification.save()

        return Response(status=status.HTTP_200_OK)


# Would it be better to reduce duplication with the View above?
class PasswordEmailVerifySendView(APIView):
    permission_classes = (permissions.AllowAny,)

    def post(self, request):
        serializer = UserCheckSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        email = serializer.validated_data['email']
        code = generate_code()
        message_data = message(code)

        mail_title = "Speech Buddy 비밀번호 재설정 메일입니다."
        mail_to = email
        EmailMessage(mail_title, message_data, to=[mail_to]).send()

        if not EmailVerification.objects.filter(email=email).exists():
            EmailVerification.objects.create(email=email, code=code)
        else:
            verification = EmailVerification.objects.get(email=email)
            verification.code = code
            verification.save()

        return Response(status=status.HTTP_200_OK)


class SignUpEmailVerifyAcceptView(APIView):
    permission_classes = (permissions.AllowAny,)

    def post(self, request):
        serializer = CodeCheckSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        return Response(status=status.HTTP_200_OK)


class PasswordEmailVerifyAcceptView(APIView):
    permission_classes = (permissions.AllowAny,)

    def post(self, request):
        serializer = CodeCheckSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)

        email = serializer.validated_data.get('email')
        user = User.objects.get(email=email)
        token = TokenObtainPairSerializer.get_token(user)
        refresh = RefreshToken(str(token))
        access = str(token.access_token)

        # Blacklist refresh token
        # That is, the user only gets access token (it means temporary-login so as to utilize password-update-view)
        refresh.blacklist()

        access_token = {
            'access': access
        }

        return Response(access_token, status=status.HTTP_200_OK)


class UserLoginView(APIView):
    permission_classes = (permissions.AllowAny,)

    def post(self, request):
        serializer = UserLoginSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)

        token = serializer.validated_data['token']

        return Response(token, status=status.HTTP_200_OK)


class UserLogoutView(APIView):
    permission_classes = (permissions.IsAuthenticated, )

    def post(self, request):
        serializer = UserLogoutSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)

        refresh_token = RefreshToken(serializer.validated_data['refresh'])
        access_token = AccessToken(request.META.get('HTTP_AUTHORIZATION').split()[1])
        refresh_token.blacklist()
        access_token.blacklist()
        return Response(status=status.HTTP_200_OK)


class UserProfileView(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def get(self, request):
        user = request.user
        serialized_user = UserProfileSerializer(user).data
        response_data = {"user": serialized_user}
        return Response(response_data, status=status.HTTP_200_OK)


class PasswordUpdateView(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def patch(self, request):
        user = request.user
        data = request.data
        password_serializer = PasswordUpdateSerializer(user, data=data, context={'user': user})
        password_serializer.is_valid(raise_exception=True)
        password_serializer.save()
        return Response(status=status.HTTP_200_OK)


class NicknameUpdateView(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def patch(self, request):
        user = request.user
        data = request.data
        nickname_serializer = NicknameUpdateSerializer(user, data=data, context={'user': user})
        nickname_serializer.is_valid(raise_exception=True)
        nickname_serializer.save()
        return Response(status=status.HTTP_200_OK)


class UserWithdrawView(APIView):
    permission_classes = (permissions.IsAuthenticated, )

    def post(self, request):
        user = request.user

        # delete user's symbol images in s3
        symbols = Symbol.objects.filter(created_by=user)
        bucket_name = settings.AWS_STORAGE_BUCKET_NAME
        s3 = boto3.client(
            's3',
            aws_access_key_id=settings.AWS_ACCESS_KEY_ID,
            aws_secret_access_key=settings.AWS_SECRET_ACCESS_KEY,
        )
        for symbol in symbols:
            key = "media/" + str(symbol.image)
            try:
                s3.delete_object(Bucket=bucket_name, Key=key)
            except Exception as e:
                print(f"Error: {e}")

        # Don't know why, but if we attempt to delete the user after blacklisting the token,
        # the blacklisting doesn't work properly as expected
        request.user.delete()

        serializer = UserLogoutSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        refresh_token = RefreshToken(serializer.validated_data['refresh'])
        refresh_token.blacklist()

        return Response(status=status.HTTP_200_OK)