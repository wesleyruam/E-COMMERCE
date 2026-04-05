from sqlalchemy.orm import Session
from fastapi import HTTPException, status
from models.UserModel import User
from schemas.user_request import UserRequestDTO
from schemas.login_request import LoginRequestDTO
from util.utils import generate_token, hash_password, verify_password


def register(db: Session, user_request: UserRequestDTO) -> User:
    if db.query(User).filter(User.email == user_request.email).first():
        raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail="Email already in use")

    user = User(
        name=user_request.name,
        email=user_request.email,
        password=hash_password(user_request.password),
        token=generate_token()
    )
    db.add(user)
    db.commit()
    db.refresh(user)
    return user


def login(db: Session, login_request: LoginRequestDTO) -> User:
    user = db.query(User).filter(User.email == login_request.email).first()
    if not user or not verify_password(login_request.password, user.password):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid email or password")
    return user
