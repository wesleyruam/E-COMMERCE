from enum import Enum
from sqlalchemy import Column, Integer, String, DateTime, Float, ForeignKey, Enum as SQLEnum
from config.db import Base
from datetime import datetime, timezone


class PaymentStatusEnum(Enum):
    PENDING = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"
    REFUNDED = "REFUNDED"


class Payment(Base):
    __tablename__ = 'payments'

    id = Column(Integer, primary_key=True, index=True)
    order_id = Column(Integer, nullable=False, unique=True)  # um pedido → um pagamento
    amount = Column(Float, nullable=False)
    status = Column(SQLEnum(PaymentStatusEnum), nullable=False, default=PaymentStatusEnum.PENDING)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False)
    created_at = Column(DateTime, default=lambda: datetime.now(timezone.utc))

