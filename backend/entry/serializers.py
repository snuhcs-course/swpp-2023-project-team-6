from rest_framework import serializers
from rest_framework.exceptions import ValidationError

from entry.models import FavoriteSymbol, Symbol


class FavoriteBackupSerializer(serializers.Serializer):
    # symbol_id
    id = serializers.IntegerField(source='symbol_id', required=True)

    def validate(self, data):
        id = data.get('symbol_id')
        # Symbol table에 저장된 마지막 symbol의 id
        last_symbol_id = Symbol.objects.latest('id').id

        if id not in list(range(1, last_symbol_id+1)):
            raise ValidationError({"id": ["Invalid symbol (no such symbol)"]})

        return data

    def create(self, validated_data):
        user = self.context['user']
        symbol_id = validated_data.get('symbol_id')
        symbol = Symbol.objects.get(id=symbol_id)

        # if the symbol is not registered as favorite
        if not FavoriteSymbol.objects.filter(user=user, symbol=symbol).exists():
            FavoriteSymbol.objects.create(user=user, symbol=symbol)


