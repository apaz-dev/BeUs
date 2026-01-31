from pydantic import BaseModel, EmailStr, Field, field_validator
from datetime import datetime
from typing import Optional
import re
from app.schemas.Profile import ProfilePublic

class TeamCreate(BaseModel):
	name: str = Field(..., description="Nombre del equipo")
	owner_id: int

class TeamResponse(BaseModel):
	name: str
	join_code: str

class TeamMembersResponse(BaseModel):
    name: str
    members: list[ProfilePublic]

class TeamJoin(BaseModel):
	join_code: str

class TeamLeave(BaseModel):
	team_id: int