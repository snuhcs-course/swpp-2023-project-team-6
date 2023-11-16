import string
import random


def normalize_email(email):
    try:
        email_name, domain_part = email.strip().rsplit("@", 1)
    except ValueError:
        pass
    else:
        email = email_name + "@" + domain_part.lower()
    return email


def generate_code():
    LENGTH = 6
    seed = string.digits
    code = ""

    for i in range(LENGTH):
        code += random.choice(seed)

    return code


def message(code):
    return f"안녕하세요, 스피치버디입니다." \
           f"\n아래 인증번호를 앱에 입력하시면 이메일 인증이 완료됩니다." \
           f"\n\n인증번호: {code}" \
           f"\n\n감사합니다."
