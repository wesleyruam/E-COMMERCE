from pydantic import BaseModel, EmailStr, Field


class LoginRequestDTO(BaseModel):
    email: EmailStr = Field(..., description="Email do usuário")
    password: str = Field(..., min_length=6, description="Senha do usuário")

    class Config:
        json_schema_extra = {
            "example": {
                "email": "joao@example.com",
                "password": "senha123"
            }
        }
