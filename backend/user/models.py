from django.contrib.auth.base_user import BaseUserManager, AbstractBaseUser
from django.contrib.auth.models import PermissionsMixin
from django.db import models


class CustomUserManager(BaseUserManager):
    use_in_migrations = True

    def _create_user(self, nickname, email, password, **extra_fields):
        if not email:
            raise ValueError('이메일을 입력해주세요.')
        if not nickname:
            raise ValueError('닉네임을 입력해주세요.')

        email = self.normalize_email(email)
        user = self.model(nickname=nickname, email=email, **extra_fields)
        user.set_password(password)
        user.save(force_insert=True, using=self._db)
        return user

    def create_user(self, nickname, email, password, **extra_fields):
        extra_fields.setdefault('is_superuser', False)

        return self._create_user(nickname, email, password, **extra_fields)

    def create_superuser(self, nickname, email, password, **extra_fields):
        extra_fields.setdefault('is_superuser', True)

        if extra_fields.get('is_superuser') is not True:
            raise ValueError('권한 설정이 잘못되었습니다.')

        return self._create_user(nickname, email, password, **extra_fields)


class User(AbstractBaseUser, PermissionsMixin):
    objects = CustomUserManager()

    email = models.EmailField(max_length=50, unique=True, null=False, blank=False)
    nickname = models.CharField(max_length=15, null=False)
    is_active = models.BooleanField(default=True)
    is_superuser = models.BooleanField(default=False)
    updated_at = models.DateTimeField(null=True)
    created_at = models.DateTimeField(auto_now_add=True)

    USERNAME_FIELD = 'email'
    # python manage.py createsuperuser로 사용자를 만들 때 필수로 입력하게 되는 필드 리스트
    # USERNAME_FIELD와 password는 항상 입력이 요구됨
    REQUIRED_FIELDS = ['nickname']

    def __str__(self):
        return self.nickname

    def get_short_name(self):
        return self.email


class EmailVerification(models.Model):
    email = models.EmailField(max_length=50, unique=True, null=False, blank=False)
    code = models.CharField(max_length=6, null=False, blank=False)
    created_at = models.DateTimeField(auto_now=True)
