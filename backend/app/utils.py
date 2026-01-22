from datetime import datetime, timedelta
from typing import Optional
import hashlib
import bcrypt
from jose import JWTError, jwt
from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from sqlalchemy.orm import Session
from app.models.user import User

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