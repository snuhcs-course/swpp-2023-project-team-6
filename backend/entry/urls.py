from django.urls import path
from . import views


app_name = 'settings'
urlpatterns = [
    path('favorite/backup/', views.FavoriteBackupView.as_view(), name='backup favorite'),
]
