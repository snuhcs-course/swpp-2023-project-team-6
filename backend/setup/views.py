from rest_framework import permissions, status
from rest_framework.response import Response
from rest_framework.views import APIView
from setup.models import Setting
from setup.serializers import SettingBackupSerializer


# Create your views here.
class SettingBackupView(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def post(self, request):
        user = request.user

        serializer = SettingBackupSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        display_mode = serializer.validated_data['display_mode']
        default_menu = serializer.validated_data['default_menu']

        if not Setting.objects.filter(user=user).exists():
            Setting.objects.create(display_mode=display_mode, default_menu=default_menu, user=user)
        else:
            curr_setting = Setting.objects.get(user=user)
            curr_setting.display_mode = display_mode
            curr_setting.default_menu = default_menu
            curr_setting.save()

        return Response(status=status.HTTP_200_OK)

    def get(self, request):
        user = request.user

        if not Setting.objects.filter(user=user).exists():
            response_data = {
                "display_mode": 0,
                "default_menu": 0
            }
        else:
            curr_setting = Setting.objects.get(user=user)
            response_data = SettingBackupSerializer(curr_setting).data

        return Response(response_data, status=status.HTTP_200_OK)

