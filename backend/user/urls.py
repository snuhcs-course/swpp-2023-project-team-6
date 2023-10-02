from django.urls import path
from rest_framework_simplejwt.views import TokenRefreshView

from . import views


app_name='user'
urlpatterns = [
    path('signup/', views.UserSignUpView.as_view(), name='signup'),
    path('login/', views.UserLoginView.as_view(), name='login'),
    path('refresh/', TokenRefreshView.as_view(), name='token refresh'),
    path('logout/', views.UserLogoutView.as_view(), name='logout'),
    path('checkemail/', views.EmailCheckView.as_view(), name='check email'),
    path('profile/', views.UserProfileView.as_view(), name='profile'),
]