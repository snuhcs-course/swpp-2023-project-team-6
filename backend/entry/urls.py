from django.urls import path
from . import views


app_name = 'entry'
urlpatterns = [
    path('backup/', views.MySymbolBackupView.as_view(), name='backup my symbol'),
    path('', views.MySymbolRetrieveView.as_view(), name='get all of my symbols'),
    path('<int:pk>/', views.MySymbolRetrieveView.as_view(), name='get my specific symbol'),
    path('favorite/backup/', views.FavoriteBackupView.as_view(), name='backup favorite symbol')
]