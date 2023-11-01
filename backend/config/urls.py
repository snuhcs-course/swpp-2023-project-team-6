from django.urls import path, include
from django.contrib import admin

urlpatterns = [
    path('admin/', admin.site.urls),
    path('user/', include('user.urls')),
    path('setting/', include('setup.urls', namespace='default')),
    path('symbol/', include('entry.urls')),
]
