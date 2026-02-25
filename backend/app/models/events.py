from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.db import Base


class Event(Base):
	"""Tabla de eventos"""
	__tablename__ = "events"
	
	id = Column(Integer, primary_key=True, index=True)
	tipo = Column(String(100), nullable=False)
	fecha_inicio = Column(DateTime(timezone=True), nullable=False)
	fecha_fin = Column(DateTime(timezone=True), nullable=False)
	team_id = Column(Integer, ForeignKey('teams.id', ondelete='CASCADE'), nullable=False, index=True)
	created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
	
	# Relación con Team
	team = relationship('Team', back_populates='events')
	
	def __repr__(self):
		return f"<Event(id={self.id}, tipo={self.tipo}, team_id={self.team_id})>"
