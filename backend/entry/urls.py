from django.urls import path
from . import views


app_name = 'entry'
urlpatterns = [
    path('favorite/backup/', views.FavoriteBackupView.as_view(), name='backup favorite symbol'),
]
