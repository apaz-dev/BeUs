"""
Rutas de gestión de usuarios
"""
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from app.database import get_db
from app.models import User
from app.schemas import UserResponse, UserUpdate, Message
from app.security import get_current_user

router = APIRouter(prefix="/users", tags=["Usuarios"])


@router.get("/me", response_model=UserResponse)
async def read_current_user(current_user: User = Depends(get_current_user)):
    """
    Obtiene la información del usuario actual
    """
    return current_user


@router.put("/me", response_model=UserResponse)
async def update_current_user(
    user_update: UserUpdate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Actualiza la información del usuario actual
    """
    # Actualizar campos si se proporcionan
    if user_update.full_name is not None:
        current_user.full_name = user_update.full_name
    
    if user_update.bio is not None:
        current_user.bio = user_update.bio
    
    if user_update.avatar_url is not None:
        current_user.avatar_url = user_update.avatar_url
    
    db.commit()
    db.refresh(current_user)
    
    return current_user


@router.delete("/me", response_model=Message)
async def delete_current_user(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Desactiva la cuenta del usuario actual (soft delete)
    """
    current_user.is_active = False
    db.commit()
    
    return {"message": "Cuenta desactivada exitosamente"}


@router.get("/{username}", response_model=UserResponse)
async def get_user_by_username(
    username: str,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Obtiene la información pública de un usuario por su username
    """
    user = db.query(User).filter(User.username == username.lower()).first()
    
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Usuario no encontrado"
        )
    
    if not user.is_active:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Usuario no encontrado"
        )
    
    return user


@router.get("/", response_model=List[UserResponse])
async def search_users(
    query: str = "",
    skip: int = 0,
    limit: int = 20,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Busca usuarios por username o nombre completo
    
    - **query**: término de búsqueda
    - **skip**: número de resultados a saltar (paginación)
    - **limit**: número máximo de resultados (máximo 50)
    """
    if limit > 50:
        limit = 50
    
    if query:
        users = db.query(User).filter(
            User.is_active == True,
            (User.username.contains(query.lower()) | User.full_name.contains(query))
        ).offset(skip).limit(limit).all()
    else:
        users = db.query(User).filter(
            User.is_active == True
        ).offset(skip).limit(limit).all()
    
    return users
