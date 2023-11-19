import boto3
import botocore
from rest_framework import permissions, status
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.exceptions import ValidationError

from config import settings
from entry.models import FavoriteSymbol, Symbol
from entry.serializers import FavoriteBackupSerializer, MySymbolBackupSerializer, MySymbolEnableSerializer


class FavoriteBackupView(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def post(self, request):
        user = request.user

        symbol_ids = request.query_params.get('id')
        if symbol_ids is None:
            data = []
        else:
            unique_ids = set(symbol_ids.split(','))  # to handle duplicate values
            data = [{'id': int(x)} for x in unique_ids]

        serializer = FavoriteBackupSerializer(data=data, context={'user': user}, many=True)
        serializer.is_valid(raise_exception=True)
        serializer.save()

        # Delete favorite-symbol if user undid favorite-symbol registration
        # Load all the favorite symbols (id values) of the user
        # symbol_to_delete ultimately contains symbols to delete
        favorite_symbols = FavoriteSymbol.objects.filter(user=user).values('symbol_id')
        symbol_to_delete = [item['symbol_id'] for item in favorite_symbols]

        # Remove symbols that user tries to backup from the symbol_to_delete list
        try:
            for item in serializer.validated_data:
                symbol_to_delete.remove(item.get('symbol_id'))
        except ValueError:
            pass

        # Now, symbol_to_delete contains the symbol ids to delete
        for id in symbol_to_delete:
            symbol = Symbol.objects.get(id=id)
            FavoriteSymbol.objects.get(user=user, symbol=symbol).delete()

        return Response(status=status.HTTP_200_OK)

    def get(self, request):
        user = request.user
        favorites = FavoriteSymbol.objects.filter(user=user)
        response_data = FavoriteBackupSerializer(favorites, many=True).data
        response_data = {
            "results": response_data
        }
        return Response(response_data, status=status.HTTP_200_OK)


# Backup user-created-symbol
class MySymbolBackupView(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def post(self, request):
        user = request.user
        data = request.data
        # text, category를 key 값이 content인 json으로 받아올 때면
        # json.loads(request.POST.get('content')) 이런 식으로 사용하면 됨

        serializer = MySymbolBackupSerializer(data=data, context={'user': user})
        serializer.is_valid(raise_exception=True)
        my_symbol = serializer.save()

        response_data = {
            "id": my_symbol.id,
            "image_url": my_symbol.image.url
        }

        return Response(response_data, status=status.HTTP_200_OK)


# Get information about user-created-symbol
class MySymbolRetrieveView(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def get(self, request, pk=None):
        user = request.user

        if pk is not None:
            all_symbols = list(Symbol.objects.values_list('id', flat=True))
            if pk not in all_symbols:
                raise ValidationError({"pk": ["invalid symbol (no such symbol)"]})

            symbol = Symbol.objects.get(id=pk)
            if user != symbol.created_by:
                raise ValidationError({"not_mine": ["the requested symbol is created by another user"]})
            if symbol.is_valid == False:
                raise ValidationError({"invalid": ["the symbol is not valid yet"]})
            serialized_symbol = MySymbolBackupSerializer(symbol).data
            response_data = {"my_symbol": serialized_symbol}
        else:
            symbols = Symbol.objects.filter(created_by=user, is_valid=True)
            serialized_symbols = MySymbolBackupSerializer(symbols, many=True).data
            response_data = {"my_symbols": serialized_symbols}

        return Response(response_data, status=status.HTTP_200_OK)


# Enable symbols(backup process completes when the symbol is enabled)
class MySymbolEnableView(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def post(self, request):
        user = request.user

        symbol_ids = request.query_params.get('id')
        if symbol_ids is None:
            data = []
        else:
            unique_ids = set(symbol_ids.split(','))
            data = [{'id': int(x)} for x in unique_ids]

        serializer = MySymbolEnableSerializer(data=data, context={'user': user}, many=True)
        serializer.is_valid(raise_exception=True)

        ### After all validations are done
        # (Refer to FavoriteBackupView!)
        # Load all the mysymbols (id values) of the user
        my_symbols = Symbol.objects.filter(created_by=user).values('id')
        symbol_to_delete = [item['id'] for item in my_symbols]

        # Enabling process
        for item in serializer.validated_data:
            id = item['id']
            symbol = Symbol.objects.get(id=id)
            if not symbol.is_valid:
                symbol.is_valid = True
                symbol.save()
            symbol_to_delete.remove(id)

        # Deleting process
        for id in symbol_to_delete:
            symbol = Symbol.objects.get(id=id)

            # delete image in s3
            s3 = boto3.client(
                's3',
                aws_access_key_id=settings.AWS_ACCESS_KEY_ID,
                aws_secret_access_key=settings.AWS_SECRET_ACCESS_KEY,
            )
            key = "media/" + str(symbol.image)
            bucket_name = settings.AWS_STORAGE_BUCKET_NAME

            try:
                s3.delete_object(Bucket=bucket_name, Key=key)
            except Exception as e:
                print(f"Error: {e}")

            symbol.delete()

        return Response(status=status.HTTP_200_OK)
