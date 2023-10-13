from rest_framework import permissions
from rest_framework.views import APIView


# Create your views here.
class FavoriteBackupView(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def post(self, request):
        return

    def get(self, request):
        return

