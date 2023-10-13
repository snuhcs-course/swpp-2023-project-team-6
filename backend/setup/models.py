from django.db import models


# Create your models here.
class Setting(models.Model):
    DISPLAY_CHOICE = (
        (0, 'light'),
        (1, 'dark'),
    )

    MENU_CHOICE = (
        (0, 'symbol'),
        (1, 'tts'),
    )

    user = models.ForeignKey('user.User', related_name='settings', on_delete=models.CASCADE)
    display_mode = models.IntegerField(null=False, default=0, choices=DISPLAY_CHOICE)
    default_menu = models.IntegerField(null=False, default=0, choices=MENU_CHOICE)


