from fastapi import Header, HTTPException, Depends, status
from sqlalchemy.orm import Session
from config.db import get_db
from models.UserModel import User


def get_current_user(x_api_key: str = Header(...), db: Session = Depends(get_db)) -> User:
    user = db.query(User).filter(User.token == x_api_key).first()
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or missing API key"
        )
    return user
