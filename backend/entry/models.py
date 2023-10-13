from django.db import models

from user.models import User


# Create your models here.
class Symbol(models.Model):
    text = models.CharField(max_length=15, null=False, blank=False)
    # image = models.ImageField
    created_by = models.ForeignKey('user.User', related_name='symbol', on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)

