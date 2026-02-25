from fastapi import APIRouter, Depends, HTTPException, status, Request
from sqlalchemy.orm import Session

# SPACE
from app.db import get_db
from app.utils import (
	get_password_hash
)
from app.models.user import *
from app.models.token import *

from app.config import Settings as S

from app.schemas.Register import Register, RegisterResponse

router = APIRouter(prefix="/register", tags=["Registro"])

@router.post("/", response_model=RegisterResponse, status_code=status.HTTP_201_CREATED)
async def register(request: Request, user_data: Register, db: Session = Depends(get_db)):
    """
    Registra un nuevo usuario
    
    - **username**: nombre de usuario único (3-50 caracteres, solo alfanuméricos y guiones bajos)
    - **email**: correo electrónico único
    - **password**: contraseña (mínimo 8 caracteres, debe incluir mayúsculas, minúsculas, números y caracteres especiales)
    """
    db_user = db.query(User).filter(User.username == user_data.username.lower()).first()
    if db_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="El nombre de usuario ya está registrado"
        )
    db_user = db.query(User).filter(User.email == user_data.email.lower()).first()
    if db_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="El correo electrónico ya está registrado"
        )

    new_user = User(
        username=user_data.username.lower(),
        email=user_data.email.lower(),
        hashed_password=get_password_hash(user_data.password),
        avatar_url=f"{S.BASE_URL}/avatars/default.png"
    )
    
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    
    return new_user
