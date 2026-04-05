from pydantic import BaseModel, Field
from datetime import datetime
from enum import Enum


class PaymentStatusEnum(str, Enum):
    PENDING = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"
    REFUNDED = "REFUNDED"


class PaymentResponseDTO(BaseModel):
    """DTO para saída de pagamento (enviado para Spring Boot)"""
    payment_id: int = Field(..., description="ID único do pagamento")
    order_id: int = Field(..., description="ID do pedido")
    status: PaymentStatusEnum = Field(..., description="Status do pagamento")
    amount: float = Field(..., description="Valor do pagamento")
    created_at: datetime = Field(..., description="Data de criação do pagamento")

    class Config:
        json_schema_extra = {
            "example": {
                "payment_id": 1,
                "order_id": 1,
                "status": "APPROVED",
                "amount": 99.99,
                "created_at": "2026-04-05T10:30:00"
            }
        }
