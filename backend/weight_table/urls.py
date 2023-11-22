from django.urls import path
from . import views


app_name = 'weight_table'
urlpatterns = [
    path('backup/', views.WeightTableBackupView.as_view(), name='backup weight table'),
]