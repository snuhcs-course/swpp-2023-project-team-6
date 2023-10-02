from rest_framework import status, permissions
from rest_framework.views import APIView
from rest_framework.response import Response


from .serializers import UserSignUpSerializer


class UserSignUpView(APIView):
    permission_classes = (permissions.AllowAny,)

    def post(self, request):
        serializer = UserSignUpSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()

        return Response(status=status.HTTP_201_CREATED)