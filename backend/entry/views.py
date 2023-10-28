from rest_framework import permissions, status
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.exceptions import ValidationError

from entry.models import FavoriteSymbol, Symbol
from entry.serializers import FavoriteBackupSerializer, MySymbolBackupSerializer


class FavoriteBackupView(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def post(self, request):
        user = request.user
        data = request.data

        serializer = FavoriteBackupSerializer(data=data, context={'user': user}, many=True)
        serializer.is_valid(raise_exception=True)
        serializer.save()

        # Delete favorite-symbol if user undid favorite-symbol registration
        # Load all the favorite symbols (id values) of the user
        # symbol_ids ultimately contains symbols to delete
        favorite_symbols = FavoriteSymbol.objects.filter(user=user).values('symbol_id')
        symbol_ids = [item['symbol_id'] for item in favorite_symbols]

        # Remove symbols that user tries to backup from the symbol_ids list
        try:
            for item in serializer.validated_data:
                symbol_ids.remove(item.get('symbol_id'))
        except ValueError:
            pass

        # Now, symbol_ids contains the symbol ids to delete
        for id in symbol_ids:
            symbol_to_delete = Symbol.objects.get(id=id)
            FavoriteSymbol.objects.get(user=user, symbol=symbol_to_delete).delete()

        return Response(status=status.HTTP_200_OK)

    def get(self, request):
        user = request.user
        favorites = FavoriteSymbol.objects.filter(user=user)
        response_data = FavoriteBackupSerializer(favorites, many=True).data
        response_data = {
            "results": response_data
        }
        return Response(response_data, status=status.HTTP_200_OK)


class MySymbolBackupView(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def post(self, request):
        user = request.user
        data = request.data

        serializer = MySymbolBackupSerializer(data=data, context={'user': user})
        serializer.is_valid(raise_exception=True)
        my_symbol = serializer.save()

        response_data = {
            "id": my_symbol.id
        }

        return Response(response_data, status=status.HTTP_200_OK)


class MySymbolRetrieveView(APIView):
    def get(self, request, pk=None):
        user = request.user

        if pk is not None:
            symbol = Symbol.objects.get(id=pk)
            if user!=symbol.created_by:
                raise ValidationError({"not_mine": ["the requested symbol is created by another user"]})
            serialized_symbol = MySymbolBackupSerializer(symbol).data
            response_data = {"my_symbol": serialized_symbol}
        else:
            symbols = Symbol.objects.filter(created_by=user)
            serialized_symbols = MySymbolBackupSerializer(symbols, many=True).data
            response_data = {"my_symbols": serialized_symbols}

        return Response(response_data, status=status.HTTP_200_OK)

