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

        if id not in list(range(1, last_symbol_id + 1)):
            raise ValidationError({"id": ["Invalid symbol (no such symbol)"]})

        return data

    def create(self, validated_data):
        user = self.context['user']
        symbol_id = validated_data.get('symbol_id')
        symbol = Symbol.objects.get(id=symbol_id)

        # if the symbol is not registered as favorite
        if not FavoriteSymbol.objects.filter(user=user, symbol=symbol).exists():
            FavoriteSymbol.objects.create(user=user, symbol=symbol)


class MySymbolBackupSerializer(serializers.Serializer):
    text = serializers.CharField(required=True)
    category = serializers.IntegerField(required=True)
    image = serializers.ImageField(required=True)
    is_valid = serializers.BooleanField(read_only=True)
    created_at = serializers.DateTimeField(read_only=True)

    def validate(self, data):
        text = data.get('text')
        category = data.get('category')

        # To send custom error message
        if len(text) > 20:
            raise ValidationError({"long_text": ["word text is too long"]})

        if not (0 <= category <= 23):
            raise ValidationError({"category": ["No such category"]})

        return data

    def create(self, validated_data):
        user = self.context['user']

        text = validated_data['text']
        category = validated_data['category']
        image = validated_data['image']

        symbol = Symbol.objects.create(text=text, category=category, image=image, created_by=user)

        return symbol
