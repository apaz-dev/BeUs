from pydantic import BaseModel, EmailStr, Field, field_validator
from datetime import datetime
from typing import Optional
import re

class Register(BaseModel):
    """Esquema para crear usuario"""
    username: str = Field(..., min_length=3, max_length=12, description="Nombre que se muestra rollo nickname")
    password: str = Field(..., min_length=8, max_length=16, description="Contraseña")
    email: EmailStr = Field(..., description="Correo electronico")
    full_name: Optional[str] = Field(None, max_length=100, description="Nombre completo")
    
    @field_validator('password')
    @classmethod
    def validate_password(cls, v: str) -> str:
        if len(v) < 8:
            raise ValueError('La contraseña debe tener al menos 8 caracteres')
        if len(v) > 16:
            raise ValueError('La contraseña no puede exceder 16 caracteres')
        if not re.search(r'[A-Z]', v):
            raise ValueError('La contraseña debe contener al menos una mayúscula')
        if not re.search(r'[a-z]', v):
            raise ValueError('La contraseña debe contener al menos una minúscula')
        if not re.search(r'[0-9]', v):
            raise ValueError('La contraseña debe contener al menos un número')
        if not re.search(r'[!@#$%^&*(),.?":{}|<>]', v):
            raise ValueError('La contraseña debe contener al menos un carácter especial')
        return v
    @field_validator('username')
    @classmethod
    def validate_username(cls, v: str) -> str:
        """Validar que el username solo contenga caracteres alfanuméricos y guiones bajos"""
        if not re.match(r'^[a-zA-Z0-9_]+$', v):
            raise ValueError('El username solo puede contener letras, números y guiones bajos')
        return v.lower()
    
class RegisterResponse(BaseModel):
    username: str
    