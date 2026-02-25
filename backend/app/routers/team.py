from fastapi import APIRouter, Depends, HTTPException, status, Request
from sqlalchemy.orm import Session
import random
import string


# SPACE
from app.db import get_db
from app.utils import (
	get_current_user,
)
from app.models.teams import *
from app.models.user import User
from app.models.token import *

from app.schemas.Teams import *

router = APIRouter(prefix="/team", tags=["Teams"])

@router.put("/", response_model=TeamResponse, status_code=status.HTTP_201_CREATED)
async def create(request: Request, team_data: TeamCreate, db: Session = Depends(get_db),
				 current_user: User = Depends(get_current_user)):
	"""
	Registra un nuevo equipo
	"""

	db_team = db.query(Team).filter(Team.name == team_data.name).first()
	if db_team:
		raise HTTPException(
			status_code=status.HTTP_400_BAD_REQUEST,
			detail="El nombre del equipo ya está registrado"
		)
	new_team = Team(
		name=team_data.name,
		owner_id=current_user.id,
		join_code=''.join(random.choices(string.ascii_letters + string.digits, k=6))
	)

	db.add(new_team)
	db.commit()

	user_team = {
		"user_id": current_user.id,
		"team_id": new_team.id,
		"role": "admin"
    }

	db.execute(user_teams.insert().values(user_team))
	db.commit()
	db.refresh(new_team)

	return new_team


@router.get("/members/{team_id}", response_model=TeamMembersResponse, status_code=status.HTTP_200_OK)
async def get_team(request: Request, team_id: int, db: Session = Depends(get_db),
				   current_user: User = Depends(get_current_user)):
	"""
	Obtiene la información de un equipo por su ID pero solo si el usuario es miembro del equipo
	"""
	team = db.query(Team).filter(Team.id == team_id).first()
	if not team:
		raise HTTPException(
			status_code=status.HTTP_404_NOT_FOUND,
			detail="Equipo no encontrado."
		)
	
	if team not in current_user.teams:
		raise HTTPException(
			status_code=status.HTTP_403_FORBIDDEN,
			detail="No tienes permiso para ver los miembros de este equipo."
		)
	
	members = db.query(User).join(user_teams).filter(user_teams.c.team_id == team_id).all()
	return TeamMembersResponse(
        name=team.name,
        members=[ProfilePublic(username=member.username,
							    avatar_url=member.avatar_url) 
								for member in members]
    )

@router.put("/join", response_model=TeamMembersResponse, status_code=status.HTTP_200_OK)
async def join_team(request: Request, join_data: TeamJoin, db: Session = Depends(get_db),
				   current_user: User = Depends(get_current_user)):
	team = db.query(Team).filter(Team.join_code == join_data.join_code).first()
	if not team:
		raise HTTPException(
			status_code=status.HTTP_404_NOT_FOUND,
			detail="Código de unión inválido."
		)
	if team in current_user.teams:
		raise HTTPException(
			status_code=status.HTTP_400_BAD_REQUEST,
			detail="Ya eres miembro de este equipo."
		)
	
	team.members.append(current_user)
	db.commit()
	db.refresh(current_user)

	
	return TeamMembersResponse(
        name=team.name,
        members=[ProfilePublic(username=member.username,
							    avatar_url=member.avatar_url)
								  for member in team.members]
    )

@router.put("/leave", status_code=status.HTTP_200_OK)
async def leave_team(request: Request, leave_data: TeamLeave, db: Session = Depends(get_db),
				   current_user: User = Depends(get_current_user)):
	team = db.query(Team).filter(Team.id == leave_data.team_id).first()
	if not team:
		raise HTTPException(
			status_code=status.HTTP_404_NOT_FOUND,
			detail="Equipo no encontrado."
		)
	if team not in current_user.teams:
		raise HTTPException(
			status_code=status.HTTP_400_BAD_REQUEST,
			detail="No eres miembro de este equipo."
		)
	
	if team.owner_id == current_user.id:
		raise HTTPException(
			status_code=status.HTTP_400_BAD_REQUEST,
			detail="El propietario del equipo no puede abandonar el equipo."
		)
	
	team.members.remove(current_user)
	db.commit()
	return {"detail": "Has abandonado el equipo correctamente."}