from fastapi import APIRouter, Depends, HTTPException, status, Request
from sqlalchemy.orm import Session

# SPACE
from app.db import get_db
from app.utils import authenticate_user
from app.token import (
	create_access_token,
	create_refresh_token,
	save_refresh_token
)
from app.models.user import *
from app.models.token import *

from app.schemas.Login import Login, LoginResponse

router = APIRouter(prefix="/login", tags=["Autenticación"])


@router.post("/", response_model=LoginResponse, status_code=status.HTTP_201_CREATED)
async def login(request: Request, credentials: Login, db: Session = Depends(get_db)):
    """
    Inicia sesión con username/email y contraseña
    
    Retorna access token y refresh token
    """
    user = authenticate_user(db, credentials.username, credentials.password)
    
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Credenciales incorrectas",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    # Crear tokens
    access_token = create_access_token(data={"sub": user.id})
    refresh_token = create_refresh_token(data={"sub": user.id})
    
    # Guardar refresh token en la base de datos
    save_refresh_token(db, user.id, refresh_token)
    
    return {
        "accessToken": access_token,
        "refreshToken": refresh_token,
        "tokenType": "bearer"
    }
