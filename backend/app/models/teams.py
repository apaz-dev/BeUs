from sqlalchemy import Column, Integer, String, Boolean, DateTime, Text, ForeignKey, Table
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.db import Base
from app.models.relaciones import user_teams



class Team(Base):
	"""Tabla de equipos"""
	__tablename__ = "teams"
	
	id = Column(Integer, primary_key=True, index=True)
	join_code = Column(String, unique=True, index=True, nullable=False)
	name = Column(String(100), unique=True, index=True, nullable=False)
	description = Column(Text, nullable=True)
	created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
	members = relationship('User', secondary=user_teams, back_populates='teams')
	events = relationship('Event', back_populates='team', cascade='all, delete-orphan')
	owner_id = Column(Integer, ForeignKey('users.id', ondelete='CASCADE'), nullable=False, index=True)
	
	def __repr__(self):
		return f"<Team(id={self.id}, name={self.name})>"
