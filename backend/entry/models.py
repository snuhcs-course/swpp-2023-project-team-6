from django.db import models

from user.models import User


# Create your models here.
class Symbol(models.Model):
    text = models.CharField(max_length=15, null=False, blank=False)
    category = models.ForeignKey('Category', related_name='symbols', on_delete=models.CASCADE) # Would it be better to use SET_NULL/DEFAULT?
    # image = models.ImageField
    created_by = models.ForeignKey('user.User', related_name='symbols', on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)


class FavoriteSymbol(models.Model):
    symbol = models.ForeignKey('Symbol', related_name='favorites', on_delete=models.CASCADE)
    user = models.ForeignKey('user.User', related_name='favorites', on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)


# Would it be better to remove this category table?
# doesn't seem so useful..
class Category(models.Model):
    text = models.CharField(max_length=15, null=False, blank=False)
