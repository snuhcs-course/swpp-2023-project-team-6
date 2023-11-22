from rest_framework import serializers
from rest_framework.exceptions import ValidationError

from entry.models import FavoriteSymbol, Symbol


class FavoriteBackupSerializer(serializers.Serializer):
    # symbol_id
    id = serializers.IntegerField(source='symbol_id', required=True)

    def validate(self, data):
        id = data.get('symbol_id')
        user = self.context['user']

        all_symbols = list(Symbol.objects.values_list('id', flat=True))
        if id not in all_symbols:
            raise ValidationError({"id": ["Invalid symbol (no such symbol)"]})

        # Default symbol이 아니라면
        if id > 500:
            symbol = Symbol.objects.get(id=id)
            if symbol.created_by != user:
                raise ValidationError({"not_mine": ["the requested symbol is created by another user"]})

        return data

    def create(self, validated_data):
        user = self.context['user']
        symbol_id = validated_data.get('symbol_id')
        symbol = Symbol.objects.get(id=symbol_id)

        # if the symbol is not registered as favorite
        if not FavoriteSymbol.objects.filter(user=user, symbol=symbol).exists():
            FavoriteSymbol.objects.create(user=user, symbol=symbol)


class MySymbolBackupSerializer(serializers.Serializer):
    id = serializers.IntegerField(read_only=True)
    text = serializers.CharField(required=True)
    category = serializers.IntegerField(required=True)
    image = serializers.ImageField(required=True)
    created_at = serializers.DateTimeField(read_only=True)

    def validate(self, data):
        text = data.get('text')
        category = data.get('category')

        # To send custom error message
        if len(text) > 20:
            raise ValidationError({"long_text": ["word text is too long"]})

        if not (1 <= category <= 24):
            raise ValidationError({"category": ["No such category"]})

        return data

    def create(self, validated_data):
        user = self.context['user']

        text = validated_data['text']
        category = validated_data['category']
        image = validated_data['image']

        symbol = Symbol.objects.create(text=text, category=category, image=image, created_by=user)

        return symbol


class MySymbolEnableSerializer(serializers.Serializer):
    id = serializers.IntegerField(required=True)

    def validate(self, data):
        id = data.get('id')
        user = self.context['user']

        all_symbols = list(Symbol.objects.values_list('id', flat=True))
        if id not in all_symbols:
            raise ValidationError({"id": ["Invalid symbol (no such symbol)"]})

        if id <= 500:
            raise ValidationError({"id": ["Default symbol"]})

        # Default symbol이 아닌 경우
        symbol = Symbol.objects.get(id=id)
        if symbol.created_by != user:
            raise ValidationError({"not_mine": ["the requested symbol is created by another user"]})

        return data
