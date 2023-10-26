from django.urls import path
from rest_framework_simplejwt.views import TokenRefreshView

from . import views


app_name='user'
urlpatterns = [
    path('signup/', views.UserSignUpView.as_view(), name='signup'),
    path('validateemail/signup/send/', views.SignUpEmailVerifySendView.as_view(), name='send email verification code for signup'),
    path('validateemail/pw/send/', views.PasswordEmailVerifySendView.as_view(), name='send email verification code for forgot-pw'),
    path('validateemail/signup/accept/', views.SignUpEmailVerifyAcceptView.as_view(), name='accept email verification code for signup'),
    path('validateemail/pw/accept/', views.PasswordEmailVerifyAcceptView.as_view(), name='accept email verification code for forgot-pw'),
    path('login/', views.UserLoginView.as_view(), name='login'),
    path('refresh/', TokenRefreshView.as_view(), name='token refresh'),
    path('logout/', views.UserLogoutView.as_view(), name='logout'),
    path('profile/', views.UserProfileView.as_view(), name='get profile'),
    path('profile/nickname/', views.NicknameUpdateView.as_view(), name='change nickname'),
    path('profile/password/', views.PasswordUpdateView.as_view(), name='change password'),
]
