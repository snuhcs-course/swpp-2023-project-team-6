from rest_framework import status, permissions
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework_simplejwt.tokens import RefreshToken

from config.utils import AccessToken

from .serializers import UserSignUpSerializer, UserLoginSerializer, UserLogoutSerializer, EmailCheckSerializer, \
    UserProfileSerializer, UserProfileUpdateSerializer


class UserSignUpView(APIView):
    permission_classes = (permissions.AllowAny,)

    def post(self, request):
        serializer = UserSignUpSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()

        return Response(status=status.HTTP_201_CREATED)


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


class EmailCheckView(APIView):
    permission_classes = (permissions.AllowAny,)

    def get(self, request):
        serializer = EmailCheckSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        return Response(status=status.HTTP_200_OK)


class UserProfileView(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def get(self, request):
        user = request.user
        return Response(UserProfileSerializer(user).data, status=status.HTTP_200_OK)

    def patch(self, request):
        user = request.user
        data = request.data
        serializer = UserProfileUpdateSerializer(user, data=data, context={'user': user})
        print(data)
        serializer.is_valid(raise_exception=True)
        print("------")
        print(serializer.validated_data)
        serializer.save()

        return Response(status=status.HTTP_200_OK)