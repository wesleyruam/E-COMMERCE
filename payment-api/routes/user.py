from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session
from config.db import get_db
from schemas.user_request import UserRequestDTO
from schemas.login_request import LoginRequestDTO
from schemas.user_response import UserResponseDTO
from services import user_service

router = APIRouter(prefix="/users", tags=["Users"])


@router.post(
    "/register",
    response_model=UserResponseDTO,
    status_code=status.HTTP_201_CREATED,
    summary="Cadastra um novo usuário",
    description="Cria um usuário e retorna o token de autenticação (X-API-KEY)"
)
def register(user_request: UserRequestDTO, db: Session = Depends(get_db)):
    user = user_service.register(db, user_request)
    return UserResponseDTO(id=user.id, name=user.name, email=user.email, token=user.token)


@router.post(
    "/login",
    response_model=UserResponseDTO,
    summary="Autentica um usuário",
    description="Valida as credenciais e retorna o token de autenticação"
)
def login(login_request: LoginRequestDTO, db: Session = Depends(get_db)):
    user = user_service.login(db, login_request)
    return UserResponseDTO(id=user.id, name=user.name, email=user.email, token=user.token)
