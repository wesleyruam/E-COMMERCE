from pydantic import BaseModel, Field


class PaymentRequestDTO(BaseModel):
    """DTO para entrada de pagamento (recebido do Spring Boot)"""
    order_id: int = Field(..., gt=0, description="ID do pedido")
    amount: float = Field(..., gt=0, description="Valor do pagamento")

    class Config:
        json_schema_extra = {
            "example": {
                "order_id": 1,
                "amount": 99.99
            }
        }
