from rest_framework import permissions, status
from rest_framework.response import Response
from rest_framework.views import APIView

from weight_table.models import WeightTable
from weight_table.serializers import WeightTableBackupSerializer


class WeightTableBackupView(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def post(self, request):
        user = request.user
        data = request.data.get('weight_table')

        serializer = WeightTableBackupSerializer(data=data, context={'user': user}, many=True)
        serializer.is_valid(raise_exception=True)
        serializer.save()

        return Response(status=status.HTTP_200_OK)

    def get(self, request):
        user = request.user
        weight_table = WeightTable.objects.filter(user=user)
        response_data = WeightTableBackupSerializer(weight_table, many=True).data
        response_data = {
            "weight_table": response_data
        }
        return Response(response_data, status=status.HTTP_200_OK)
