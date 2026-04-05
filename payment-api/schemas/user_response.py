from pydantic import BaseModel, Field


class UserResponseDTO(BaseModel):
    """DTO para saída de usuário (senha nunca é retornada)"""
    id: int = Field(..., description="ID único do usuário")
    name: str = Field(..., description="Nome do usuário")
    email: str = Field(..., description="Email do usuário")
    token: str = Field(..., description="Token de autenticação")

    class Config:
        json_schema_extra = {
            "example": {
                "id": 1,
                "name": "João Silva",
                "email": "joao@example.com",
                "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            }
        }
