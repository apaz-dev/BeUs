from fastapi import APIRouter, Depends, HTTPException, status, Request
from sqlalchemy.orm import Session

# SPACE
from app.db import get_db
from app.utils import check_access_token, get_current_user
from app.token import (
	create_access_token,
	create_refresh_token,
	save_refresh_token
)
from app.models.user import *
from app.models.token import *

from app.schemas.Profile import ProfilePublic, ProfilePrivate, ProfileTeams

router = APIRouter(prefix="/profile", tags=["Profile"])

@router.get("/public", response_model=ProfilePublic, status_code=status.HTTP_200_OK)
async def get_public_profile(user_id: int, db: Session = Depends(get_db)):
	"""
	Obtiene el perfil público de un usuario por su ID
	"""
	user = db.query(User).filter(User.id == user_id).first()
	if not user:
		raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Usuario no encontrado")
	return ProfilePublic(
        username=user.username,
		avatar_url=user.avatar_url
	)

@router.get("/me", response_model=ProfilePrivate, status_code=status.HTTP_200_OK)
async def get_private_profile(request: Request, db: Session = Depends(get_db), 
							  current_user: User = Depends(get_current_user)):
	"""
	Obtiene el perfil privado del usuario autenticado
	"""
	user = db.query(User).filter(User.id == current_user.id).first()
	if not user:
		raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Usuario no encontrado")
	return ProfilePrivate(
		username=user.username,
		email=user.email,
		avatar_url=user.avatar_url,
		teams=[ProfileTeams(name=team.name, join_code=team.join_code) for team in user.teams]
	)