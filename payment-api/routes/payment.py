from fastapi import APIRouter, Depends, status
from typing import List
from sqlalchemy.orm import Session
from config.db import get_db
from auth import get_current_user
from models.UserModel import User
from schemas.payment_request import PaymentRequestDTO
from schemas.payment_response import PaymentResponseDTO
from services import payment_service

router = APIRouter(prefix="/payment", tags=["Payment"])


@router.post(
    "/",
    response_model=PaymentResponseDTO,
    status_code=status.HTTP_201_CREATED,
    summary="Inicia um pagamento",
    description="Cria um novo pagamento com status PENDING"
)
def create_payment(
    payment_request: PaymentRequestDTO,
    db: Session = Depends(get_db),
    user: User = Depends(get_current_user)
):
    return payment_service.create_payment(db, payment_request, user)


@router.get(
    "/pending",
    response_model=List[PaymentResponseDTO],
    summary="Lista pagamentos pendentes",
    description="Retorna todos os pagamentos com status PENDING do usuário autenticado"
)
def get_pending_payments(
    db: Session = Depends(get_db),
    user: User = Depends(get_current_user)
):
    return payment_service.get_pending_payments(db, user)


@router.get(
    "/{payment_id}",
    response_model=PaymentResponseDTO,
    summary="Consulta status do pagamento"
)
def get_payment(
    payment_id: int,
    db: Session = Depends(get_db),
    user: User = Depends(get_current_user)
):
    return payment_service.get_payment(db, payment_id, user)


@router.patch(
    "/{payment_id}/approve",
    response_model=PaymentResponseDTO,
    summary="Aprova um pagamento"
)
def approve_payment(
    payment_id: int,
    db: Session = Depends(get_db),
    user: User = Depends(get_current_user)
):
    return payment_service.approve_payment(db, payment_id, user)


@router.patch(
    "/{payment_id}/reject",
    response_model=PaymentResponseDTO,
    summary="Rejeita um pagamento"
)
def reject_payment(
    payment_id: int,
    db: Session = Depends(get_db),
    user: User = Depends(get_current_user)
):
    return payment_service.reject_payment(db, payment_id, user)

