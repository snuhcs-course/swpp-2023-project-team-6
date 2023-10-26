from rest_framework import serializers
from rest_framework.exceptions import ValidationError

from setup.models import Setting


class SettingBackupSerializer(serializers.Serializer):
    display_mode = serializers.IntegerField(required=True)
    default_menu = serializers.IntegerField(required=True)

    def validate(self, data):
        display_mode = data.get('display_mode')
        default_menu = data.get('default_menu')
        if display_mode not in [0, 1]:
            raise ValidationError({"display_mode": ["invalid display_mode (not 0 or 1)"]})
        if default_menu not in [0, 1]:
            raise ValidationError({"default_menu": ["invalid default_menu (not 0 or 1)"]})
        return data
