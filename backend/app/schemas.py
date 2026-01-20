"""
Esquemas de validación con Pydantic
"""
from pydantic import BaseModel, EmailStr, Field, field_validator
from datetime import datetime
from typing import Optional
import re


class UserBase(BaseModel):
    """Esquema base de usuario"""
    username: str = Field(..., min_length=3, max_length=50, description="Nombre de usuario")
    email: EmailStr = Field(..., description="Correo electrónico")
    full_name: Optional[str] = Field(None, max_length=100, description="Nombre completo")
    
    @field_validator('username')
    @classmethod
    def validate_username(cls, v: str) -> str:
        """Validar que el username solo contenga caracteres alfanuméricos y guiones bajos"""
        if not re.match(r'^[a-zA-Z0-9_]+$', v):
            raise ValueError('El username solo puede contener letras, números y guiones bajos')
        return v.lower()


class UserCreate(UserBase):
    """Esquema para crear usuario"""
    password: str = Field(..., min_length=8, max_length=128, description="Contraseña")
    
    @field_validator('password')
    @classmethod
    def validate_password(cls, v: str) -> str:
        """Validar que la contraseña sea segura"""
        if len(v) < 8:
            raise ValueError('La contraseña debe tener al menos 8 caracteres')
        if len(v) > 128:
            raise ValueError('La contraseña no puede exceder 128 caracteres')
        if not re.search(r'[A-Z]', v):
            raise ValueError('La contraseña debe contener al menos una mayúscula')
        if not re.search(r'[a-z]', v):
            raise ValueError('La contraseña debe contener al menos una minúscula')
        if not re.search(r'[0-9]', v):
            raise ValueError('La contraseña debe contener al menos un número')
        if not re.search(r'[!@#$%^&*(),.?":{}|<>]', v):
            raise ValueError('La contraseña debe contener al menos un carácter especial')
        return v


class UserLogin(BaseModel):
    """Esquema para login"""
    username: str = Field(..., description="Username o email")
    password: str = Field(..., description="Contraseña")


class UserResponse(UserBase):
    """Esquema de respuesta de usuario"""
    id: int
    bio: Optional[str] = None
    avatar_url: Optional[str] = None
    is_active: bool
    is_verified: bool
    created_at: datetime
    updated_at: datetime
    last_login: Optional[datetime] = None
    
    class Config:
        from_attributes = True


class UserUpdate(BaseModel):
    """Esquema para actualizar usuario"""
    full_name: Optional[str] = Field(None, max_length=100)
    bio: Optional[str] = Field(None, max_length=500)
    avatar_url: Optional[str] = Field(None, max_length=255)


class Token(BaseModel):
    """Esquema de respuesta de token"""
    access_token: str
    refresh_token: str
    token_type: str = "bearer"


class TokenRefresh(BaseModel):
    """Esquema para refrescar token"""
    refresh_token: str


class Message(BaseModel):
    """Esquema de mensaje genérico"""
    message: str


class HealthCheck(BaseModel):
    """Esquema de health check"""
    status: str
    timestamp: datetime
    database: str
