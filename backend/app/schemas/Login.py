from pydantic import BaseModel, EmailStr, Field, field_validator
from datetime import datetime
from typing import Optional
import re


class Login(BaseModel):
	username: str = Field(..., description="Ahora solo funciona con email pero añadir username tmb")
	password: str = Field(..., description="Contraseña")

class LoginResponse(BaseModel):
	accessToken: str
	refreshToken: str
	tokenType: str = "bearer"