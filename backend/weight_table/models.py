from django.db import models


# Create your models here.

class WeightTable(models.Model):
    symbol_id = models.IntegerField(null=False, blank=False)
    weight = models.TextField(null=False, blank=False)
    user = models.ForeignKey('user.User', related_name='weight_table', on_delete=models.CASCADE)
    updated_at = models.DateTimeField(auto_now=True)
