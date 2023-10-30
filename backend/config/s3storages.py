from storages.backends.s3boto3 import S3Boto3Storage


# settings.py에서 static과 media에 storages.backends.s3boto3.S3Boto3Storage를 사용할 수 있지만
# 그러면 둘이 같은 경로에서 관리되므로 여기서 분리해줌 (저장 경로 관련된 부분)
# 우리 앱은 static 쓸 일 크게 없을 것 같긴 하지만..
class MediaStorage(S3Boto3Storage):
    location = 'media'
    file_overwrite = False


class StaticStorage(S3Boto3Storage):
    location = 'static'
    file_overwrite = False
