from fastapi import APIRouter, Depends, HTTPException, status, Request
from sqlalchemy.orm import Session

# SPACE
from app.db import get_db
from app.utils import check_access_token
from app.token import (
	create_access_token,
	create_refresh_token,
	save_refresh_token
)
from app.models.user import *
from app.models.token import *

from app.schemas.Login import Login, LoginResponse

router = APIRouter(prefix="/token", tags=["Autenticación"])

@router.get("/check", status_code=status.HTTP_200_OK)
async def check_token(request: Request, db: Session = Depends(get_db),
					  current_user: User = Depends(check_access_token)):
	"""
	Verifica si el token de acceso es válido
	"""

	return {"message": "Token válido", "user_id": current_user.id}