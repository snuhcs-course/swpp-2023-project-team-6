from django.db import models


# Create your models here.
class Setting(models.Model):
    DISPLAY_CHOICE = (
        (0, 'light'),
        (1, 'dark'),
    )

    MENU_CHOICE = (
        (0, 'tts'),
        (1, 'symbol'),
    )

    BACKUP_CHOICE = (
        (0, 'non-auto'),
        (1, 'auto'),
    )

    user = models.OneToOneField('user.User', related_name='settings', on_delete=models.CASCADE)
    display_mode = models.IntegerField(null=False, default=0, choices=DISPLAY_CHOICE)
    default_menu = models.IntegerField(null=False, default=0, choices=MENU_CHOICE)
    auto_backup = models.IntegerField(null=False, default=1, choices=BACKUP_CHOICE)
    updated_at = models.DateTimeField(auto_now=True)


