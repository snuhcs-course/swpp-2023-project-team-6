from django.urls import path
from . import views


app_name = 'setup'
urlpatterns = [
    path('backup/', views.SettingBackupView.as_view(), name='backup settings'),
]
