"""
Configuración de la aplicación
"""
from pydantic_settings import BaseSettings
from pydantic import Field


class Settings(BaseSettings):
    """Configuración de la aplicación desde variables de entorno"""
    
    # Seguridad
    SECRET_KEY: str = Field(..., description="Clave secreta para JWT")
    ALGORITHM: str = Field(default="HS256", description="Algoritmo de encriptación")
    ACCESS_TOKEN_EXPIRE_MINUTES: int = Field(default=60, description="Minutos de expiración del access token")
    REFRESH_TOKEN_EXPIRE_DAYS: int = Field(default=7, description="Días de expiración del refresh token")
    
    # Base de datos
    DATABASE_URL: str = Field(default="sqlite:///./beus.db", description="URL de conexión a la base de datos")
    
    # Servidor
    HOST: str = Field(default="0.0.0.0", description="Host del servidor")
    PORT: int = Field(default=8443, description="Puerto del servidor")
    SSL_KEYFILE: str = Field(default="certs/key.pem", description="Ruta al archivo de clave SSL")
    SSL_CERTFILE: str = Field(default="certs/cert.pem", description="Ruta al archivo de certificado SSL")
    
    # Rate limiting
    RATE_LIMIT: str = Field(default="20/minute", description="Límite de solicitudes")
    
    class Config:
        env_file = ".env"
        case_sensitive = True


settings = Settings()
print(f"SECRET_KEY cargada: {settings.SECRET_KEY[:10]}...")