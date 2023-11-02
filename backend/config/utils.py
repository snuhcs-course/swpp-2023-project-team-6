from rest_framework_simplejwt.tokens import BlacklistMixin, AccessToken

class AccessToken(BlacklistMixin, AccessToken):
    pass
