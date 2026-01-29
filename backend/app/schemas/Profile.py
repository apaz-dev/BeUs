from pydantic import BaseModel, EmailStr, Field, field_validator
from datetime import datetime
from typing import Optional
import re

class ProfilePrivate(BaseModel):
	username: str
	email: EmailStr
	avatar_url: str

class ProfilePublic(BaseModel):
	username: str
	avatar_url: str