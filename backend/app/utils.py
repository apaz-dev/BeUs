from datetime import datetime, timedelta
from typing import Optional
import hashlib
import bcrypt
from jose import JWTError, jwt
from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from sqlalchemy.orm import Session
from app.models.user import User
from app.token import decode_token
from app.db import get_db


security = HTTPBearer()


def get_password_hash(password: str) -> str:
    """
    Genera el hash de una contraseña.
    Usa SHA256 + bcrypt para máxima seguridad y compatibilidad.
    """
    # Pre-hash con SHA256 para evitar el límite de 72 bytes de bcrypt
    password_hash = hashlib.sha256(password.encode('utf-8')).hexdigest()
    # Generar salt y hash con bcrypt
    salt = bcrypt.gensalt(rounds=12)
    hashed = bcrypt.hashpw(password_hash.encode('utf-8'), salt)
    return hashed.decode('utf-8')


def verify_password(plain_password: str, hashed_password: str) -> bool:
    password_hash = hashlib.sha256(plain_password.encode('utf-8')).hexdigest()
    return bcrypt.checkpw(password_hash.encode('utf-8'), hashed_password.encode('utf-8'))

def authenticate_user(db: Session, username: str, password: str) -> Optional[User]:
    """
    Autentica un usuario por username/email y contraseña
    
    Args:
        db: Sesión de base de datos
        username: Username o email del usuario
        password: Contraseña en texto plano
    
    Returns:
        Usuario si las credenciales son correctas, None en caso contrario
    """
    users = db.query(User).filter(
        (User.username == username.lower()) | (User.email == username.lower())
    ).first()
    
    if not users:
        return None
    
    if not verify_password(password, users.hashed_password):
        return None
    
    return users

async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(security),
    db: Session = Depends(get_db)
) -> User:
    """
    Obtiene el usuario actual desde el token de acceso
    """
    token = credentials.credentials
    payload = decode_token(token)
    
    # Verificar que sea un access token
    if payload.get("type") != "access":
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Tipo de token inválido",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    user_id_str: str = payload.get("sub")
    if user_id_str is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token inválido",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    try:
        user_id = int(user_id_str)
    except ValueError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token inválido",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    user = db.query(User).filter(User.id == user_id).first()
    if user is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Usuario no encontrado",
            headers={"WWW-Authenticate": "Bearer"},
        )
    return user

async def check_access_token(
	credentials: HTTPAuthorizationCredentials = Depends(security),
	db: Session = Depends(get_db)
) -> User:
	"""
	Verifica el token de acceso y retorna el usuario asociado
	"""
	token = credentials.credentials
	try:
		payload = decode_token(token)
		user_id: int = payload.get("sub")
		if user_id is None:
			raise HTTPException(
				status_code=status.HTTP_401_UNAUTHORIZED,
				detail="Token inválido"
			)
	except JWTError:
		raise HTTPException(
			status_code=status.HTTP_401_UNAUTHORIZED,
			detail="Token inválido"
		)

	user = db.query(User).filter(User.id == user_id).first()
	if user is None:
		raise HTTPException(
			status_code=status.HTTP_401_UNAUTHORIZED,
			detail="Usuario no encontrado"
		)
	return user