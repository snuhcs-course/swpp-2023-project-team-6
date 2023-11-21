from rest_framework import serializers
from rest_framework.exceptions import ValidationError

from entry.models import Symbol
from weight_table.models import WeightTable


class WeightTableBackupSerializer(serializers.Serializer):
    # symbol_id
    id = serializers.IntegerField(source='symbol_id', required=True)
    weight = serializers.CharField(required=True)

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
        weight = validated_data.get('weight')

        if not WeightTable.objects.filter(user=user, symbol_id=symbol_id).exists():
            WeightTable.objects.create(user=user, symbol_id=symbol_id, weight=weight)
        else:
            weight_row = WeightTable.objects.get(user=user, symbol_id=symbol_id)
            weight_row.weight = weight
            weight_row.save()
