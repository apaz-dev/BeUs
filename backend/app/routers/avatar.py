from fastapi import APIRouter, UploadFile, File, HTTPException, Depends
from fastapi.staticfiles import StaticFiles
from fastapi.responses import JSONResponse
from pathlib import Path
from sqlalchemy.orm import Session
import uuid

from app.db import get_db
from app.config import Settings as S
from app.models.user import User
from app.utils import (
	get_current_user,
)

UPLOAD_DIR = Path("uploads/avatars")
UPLOAD_DIR.mkdir(parents=True, exist_ok=True)

ALLOWED = {"image/png", "image/jpeg", "image/webp"}
MAX_MB = 3

router = APIRouter(prefix="/avatar", tags=["Avatar"])

@router.post("/upload")
async def upload_avatar(file: UploadFile = File(...), 
                        db: Session = Depends(get_db),
                        user: User = Depends(get_current_user)):
    if file.content_type not in ALLOWED:
        raise HTTPException(400, detail="Formato no permitido (png/jpg/webp)")

    data = await file.read()
    if len(data) > MAX_MB * 1024 * 1024:
        raise HTTPException(413, detail="Archivo demasiado grande")

    ext = {
        "image/png": "png",
        "image/jpeg": "jpg",
        "image/webp": "webp",
    }[file.content_type]

    filename = f"{user.id}_{uuid.uuid4().hex}.{ext}"
    path = UPLOAD_DIR / filename
    path.write_bytes(data)

    avatar_url = f"{S.BASE_URL}/avatars/{filename}"

    user.avatar_url = avatar_url
    db.add(user)
    db.commit()
    db.refresh(user)
    
    return JSONResponse({"avatar_url": avatar_url})