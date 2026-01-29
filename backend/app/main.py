import logging
from fastapi import FastAPI, Request, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.trustedhost import TrustedHostMiddleware
from app.routers import login, register, team, token, avatar, profile
from contextlib import asynccontextmanager
from app.db import engine, Base
from fastapi.staticfiles import StaticFiles

@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Gestión del ciclo de vida de la aplicación
    """
    # Startup: Crear tablas de base de datos
    logger.info("Iniciando aplicación BeUs Backend...")
    logger.info("Creando tablas de base de datos...")
    Base.metadata.create_all(bind=engine)
    logger.info("Base de datos inicializada correctamente")
    
    yield
    
    # Shutdown
    logger.info("Cerrando aplicación BeUs Backend...")

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


app = FastAPI(
	title="BeUs API",
    description="Backend para red social BeUs by PAZ=)",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc",
    lifespan=lifespan
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:3000",
        "http://localhost:8080",
        "http://127.0.0.1:3000",
        "http://127.0.0.1:8080",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Esto te protege de ataques de HostHeader Poisonin 
app.add_middleware(
    TrustedHostMiddleware,
    #allowed_hosts=["localhost", "127.0.0.1", "0.0.0.0", "10.0.2.2"]
    allowed_hosts=["*"]
)

app.include_router(login.router)
app.include_router(register.router)
app.include_router(team.router)
app.include_router(token.router)
app.include_router(avatar.router)
app.include_router(profile.router)

# Montar el directorio de archivos subidos
app.mount("/avatars", StaticFiles(directory="uploads/avatars"), name="avatars")

@app.get("/", tags=["General"])
async def root():
    """
    Endpoint raíz
    """
    return {
        "message": "BeUs API - Backend de Red Social",
        "version": "1.0.0",
        "docs": "/docs",
        "health": "/health"
    }


if __name__ == "__main__":
    import uvicorn
    
    
    uvicorn.run(
        "app.main:app",
        host="0.0.0.0",
        port=8443,
        reload=True,  # Auto-reload en desarrollo
        log_level="info",
        ssl_keyfile="certs/key.pem",
        ssl_certfile="SSL_CERTFILE=certs/cert.pem"
    )


"""
python -m uvicorn app.main:app \
  --reload \
  --host 0.0.0.0 \
  --port 8443 \
  --ssl-keyfile=certs/key.pem \
  --ssl-certfile=certs/cert.pem
"""