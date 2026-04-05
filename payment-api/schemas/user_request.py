from pydantic import BaseModel, EmailStr, Field


class UserRequestDTO(BaseModel):
    """DTO para entrada de usuário"""
    name: str = Field(..., min_length=1, max_length=255, description="Nome do usuário")
    email: EmailStr = Field(..., description="Email único do usuário")
    password: str = Field(..., min_length=6, description="Senha do usuário (mínimo 6 caracteres)")

    class Config:
        json_schema_extra = {
            "example": {
                "name": "João Silva",
                "email": "joao@example.com",
                "password": "senha123"
            }
        }
