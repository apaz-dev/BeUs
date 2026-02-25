from sqlalchemy import create_engine, event
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.declarative import declarative_base
from app.config import Settings

# Crear engine de SQLite
engine = create_engine(
    Settings.DATABASE_URL,
    connect_args={"check_same_thread": False},  # Necesario para SQLite
    pool_pre_ping=True,  # Verifica conexiones antes de usarlas
    pool_size=5,  # Tamaño pequeño para ahorrar recursos
    max_overflow=10
)


# Habilitar claves foráneas en SQLite
@event.listens_for(engine, "connect")
def set_sqlite_pragma(dbapi_conn, connection_record):
    cursor = dbapi_conn.cursor()
    cursor.execute("PRAGMA foreign_keys=ON")
    cursor.close()


# Crear sesión
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()

def get_db():
    """
		obtener una sesión de base de datos
    """
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
