import os
from django.utils import timezone
from uuid import uuid4

from django.db import models

from user.models import User


# 저장 경로 설정
def img_upload_func(instance, filename):
    prefix = 'symbol/user_{}/'.format(instance.created_by.id)
    ymd_path = timezone.now().strftime('%Y%m%d')
    file_name = uuid4().hex  # random string
    extension = os.path.splitext(filename)[-1].lower()  # 확장자 추출
    return "".join(
        [prefix, ymd_path, file_name, extension, ]
    )


class Symbol(models.Model):
    text = models.CharField(max_length=15, null=False, blank=False)
    category = models.ForeignKey('Category', related_name='symbols', on_delete=models.CASCADE) # Would it be better to use SET_NULL/DEFAULT?
    image = models.ImageField(blank=True, upload_to=img_upload_func)
    created_by = models.ForeignKey('user.User', related_name='symbols', on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)
    is_valid = models.BooleanField(default=False)


class FavoriteSymbol(models.Model):
    symbol = models.ForeignKey('Symbol', related_name='favorites', on_delete=models.CASCADE)
    user = models.ForeignKey('user.User', related_name='favorites', on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)


# Would it be better to remove this category table?
# doesn't seem so useful..
class Category(models.Model):
    text = models.CharField(max_length=15, null=False, blank=False)
