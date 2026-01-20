from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from app.database import get_db
from app.models import Team, User, user_teams
from app.schemas import TeamCreate, TeamResponse, TeamMembersResponse
from app.security import get_current_user

router = APIRouter(prefix="/teams", tags=["Equipos"])


@router.put("/create", response_model=TeamResponse, status_code=status.HTTP_201_CREATED)
async def create_new_team(
	team: TeamCreate,
	db: Session = Depends(get_db),
	current_user: User = Depends(get_current_user)
):
	"""
	Crea un nuevo equipo y asigna al usuario actual como propietario
	"""
	# Verificar si el nombre del equipo ya existe
	existing_team = db.query(Team).filter(Team.name == team.name).first()
	if existing_team:
		raise HTTPException(
			status_code=status.HTTP_400_BAD_REQUEST,
			detail="El nombre del equipo ya está en uso."
		)
	
	# Crear el equipo
	new_team = Team(
		name=team.name,
		description=team.description,
		owner_id=current_user.id
	)
	db.add(new_team)
	db.commit()
	db.refresh(new_team)
	
	current_user.teams.append(new_team)
	db.commit()
	
	return new_team

@router.put("/join/{team_id}", response_model=TeamResponse)
async def join_team(
	team_id: int,
	db: Session = Depends(get_db),
	current_user: User = Depends(get_current_user)
):
	"""
	Permite al usuario actual unirse a un equipo existente
	"""
	team = db.query(Team).filter(Team.id == team_id).first()
	if not team:
		raise HTTPException(
			status_code=status.HTTP_404_NOT_FOUND,
			detail="Equipo no encontrado."
		)
	
	if team in current_user.teams:
		raise HTTPException(
			status_code=status.HTTP_400_BAD_REQUEST,
			detail="Ya eres miembro de este equipo."
		)
	
	current_user.teams.append(team)
	db.commit()
	
	return team

@router.get("/team-members/{team_id}", response_model=TeamMembersResponse)
async def get_team_members(
	team_id: int,
	db: Session = Depends(get_db),
	current_user: User = Depends(get_current_user)
):
	"""
	Obtiene la lista de miembros de un equipo específico solo si perteneces a ese equipo
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
	return members