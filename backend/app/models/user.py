from sqlalchemy import Column, Integer, String, Boolean, DateTime, Text, ForeignKey, Table
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.db import Base
from app.models.relaciones import user_teams

class User(Base):
    """Tabla de usuario"""
    __tablename__ = "users"
    
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String(50), unique=True, index=True, nullable=False)
    email = Column(String(100), unique=True, index=True, nullable=False)
    hashed_password = Column(String(255), nullable=False)
    teams = relationship('Team', secondary=user_teams, back_populates='members')
    avatar_url = Column(String(255), nullable=True)
    
    
    def __repr__(self):
        return f"<User(id={self.id}, username={self.username}, email={self.email})>"