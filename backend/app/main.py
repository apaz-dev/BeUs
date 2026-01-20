"""
Aplicación principal FastAPI para BeUs Backend
Red social - Sistema de autenticación y gestión de usuarios
"""
from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.trustedhost import TrustedHostMiddleware
from slowapi import _rate_limit_exceeded_handler
from slowapi.errors import RateLimitExceeded
from slowapi.middleware import SlowAPIMiddleware
from contextlib import asynccontextmanager
from datetime import datetime
import logging

from app.config import settings
from app.database import engine, get_db, Base
from app.routers import auth, users, teams
from app.schemas import HealthCheck
from app.routers.auth import limiter

# Configurar logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


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


# Crear aplicación FastAPI
app = FastAPI(
    title="BeUs API",
    description="Backend seguro para red social BeUs - Sistema de autenticación y gestión de usuarios",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc",
    lifespan=lifespan
)

# Configurar rate limiting
app.state.limiter = limiter
app.add_exception_handler(RateLimitExceeded, _rate_limit_exceeded_handler)
app.add_middleware(SlowAPIMiddleware)

# Middleware de CORS (ajustar origins según tus necesidades)
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:3000",
        "http://localhost:8080",
        "http://127.0.0.1:3000",
        "http://127.0.0.1:8080",
        # Añade aquí las URLs de tu app móvil cuando las tengas
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Middleware de host confiable (protección contra host header injection)
app.add_middleware(
    TrustedHostMiddleware,
    allowed_hosts=["localhost", "127.0.0.1", "0.0.0.0"]
)


# Manejador global de excepciones
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    """
    Manejador global de excepciones para evitar exponer información sensible
    """
    logger.error(f"Error no manejado: {exc}", exc_info=True)
    return JSONResponse(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        content={"detail": "Error interno del servidor"}
    )


# Incluir routers
app.include_router(auth.router)
app.include_router(users.router)
app.include_router(teams.router)


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


@app.get("/health", response_model=HealthCheck, tags=["General"])
async def health_check():
    """
    Verifica el estado de salud de la aplicación y la base de datos
    """
    try:
        # Verificar conexión a la base de datos
        db = next(get_db())
        db.execute("SELECT 1")
        db_status = "ok"
    except Exception as e:
        logger.error(f"Error en health check de base de datos: {e}")
        db_status = "error"
    
    return {
        "status": "ok" if db_status == "ok" else "degraded",
        "timestamp": datetime.utcnow(),
        "database": db_status
    }


if __name__ == "__main__":
    import uvicorn
    
    logger.info(f"Iniciando servidor en https://{settings.HOST}:{settings.PORT}")
    logger.info(f"Documentación disponible en https://localhost:{settings.PORT}/docs")
    
    uvicorn.run(
        "app.main:app",
        host=settings.HOST,
        port=settings.PORT,
        reload=True,  # Auto-reload en desarrollo
        ssl_keyfile=settings.SSL_KEYFILE,
        ssl_certfile=settings.SSL_CERTFILE,
        log_level="info"
    )
