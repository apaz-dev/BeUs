from pydantic import BaseModel, EmailStr, Field, field_validator
from datetime import datetime
from typing import Optional
import re

class ProfileTeams(BaseModel):
	name: str
	join_code: str
	
class ProfilePrivate(BaseModel):
	username: str
	email: EmailStr
	avatar_url: str
	# Equipos a los que pertenece el usuario
	teams: Optional[list[ProfileTeams]] = Field(default_factory=list)



class ProfilePublic(BaseModel):
	username: str
	avatar_url: str