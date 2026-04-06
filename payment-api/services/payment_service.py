from sqlalchemy.orm import Session
from fastapi import HTTPException, status
from models.PaymentModel import Payment, PaymentStatusEnum
from models.UserModel import User
from schemas.payment_request import PaymentRequestDTO
from schemas.payment_response import PaymentResponseDTO


def _to_dto(payment: Payment) -> PaymentResponseDTO:
    return PaymentResponseDTO(
        payment_id=payment.id,
        order_id=payment.order_id,
        amount=payment.amount,
        status=payment.status,
        created_at=payment.created_at
    )


def _get_and_validate(db: Session, payment_id: int, user: User) -> Payment:
    payment = db.query(Payment).filter(Payment.id == payment_id).first()
    if not payment:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Payment not found")
    if payment.user_id != user.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied")
    return payment


def create_payment(db: Session, payment_request: PaymentRequestDTO, user: User) -> PaymentResponseDTO:
    existing = db.query(Payment).filter(Payment.order_id == payment_request.order_id).first()
    if existing:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=f"A payment for order {payment_request.order_id} already exists"
        )

    payment = Payment(
        order_id=payment_request.order_id,
        amount=payment_request.amount,
        user_id=user.id,
        status=PaymentStatusEnum.PENDING
    )
    db.add(payment)
    db.commit()
    db.refresh(payment)
    return _to_dto(payment)


def get_payment(db: Session, payment_id: int, user: User) -> PaymentResponseDTO:
    return _to_dto(_get_and_validate(db, payment_id, user))


def approve_payment(db: Session, payment_id: int, user: User) -> PaymentResponseDTO:
    payment = _get_and_validate(db, payment_id, user)
    if payment.status != PaymentStatusEnum.PENDING:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Only PENDING payments can be approved. Current status: {payment.status.value}"
        )
    payment.status = PaymentStatusEnum.APPROVED
    db.commit()
    db.refresh(payment)
    return _to_dto(payment)


def reject_payment(db: Session, payment_id: int, user: User) -> PaymentResponseDTO:
    payment = _get_and_validate(db, payment_id, user)
    if payment.status != PaymentStatusEnum.PENDING:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Only PENDING payments can be rejected. Current status: {payment.status.value}"
        )
    payment.status = PaymentStatusEnum.REJECTED
    db.commit()
    db.refresh(payment)
    return _to_dto(payment)
