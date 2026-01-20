# API

COMO OBTENER TOKEN 

```
curl -k -X POST https://localhost:8443/auth/login \
  -H "Content-Type: application/json" \
  -d '{                                                 
    "username": "usuario123",
    "password": "Password123!"
  }'
```

`eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzY4OTI5NTM1LCJpYXQiOjE3Njg5Mjc3MzUsInR5cGUiOiJhY2Nlc3MifQ.JIaP4ZqaekYezXx6kniQr3Za6kFp6wYe-h0GK_dvu7Q`

```
curl -k -X POST https://localhost:8443/auth/login \
  -H "Content-Type: application/json" \
  -d '{                                                 
    "username": "usuario1234",
    "password": "Password123!"
  }'
```

`eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyIiwiZXhwIjoxNzY4OTI5NDc0LCJpYXQiOjE3Njg5Mjc2NzQsInR5cGUiOiJhY2Nlc3MifQ.Fa4r2fW6AChdj_gx8lFgYCMApCKWpDuA4mCM1U_IHxA`

---

## AUTH

### REGISTRO

`routers/auth.py`:

- Crea un nuevo usuario en el sistema
- El username debe ser único
- Contraseña mínimo 8 caracteres, debe contener mayúsculas, minúsculas, números y caracteres especiales

```
curl -k -X POST "https://localhost:8443/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nuevo_usuario",
    "email": "usuario@email.com",
    "password": "Password123!",
    "full_name": "Nombre Completo"
  }'
```

RESPUESTA:

```
id: int
username: str
email: str
full_name: str
bio: Optional[str] = None
avatar_url: Optional[str] = None
is_active: bool
is_verified: bool
created_at: datetime
updated_at: datetime
last_login: Optional[datetime] = None
```

### LOGIN

`routers/auth.py`:

- Inicia sesión con username y password
- Retorna access_token y refresh_token

```
curl -k -X POST "https://localhost:8443/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario123",
    "password": "Password123!"
  }'
```

RESPUESTA:

```
access_token: str
refresh_token: str
token_type: str = "bearer"
```

### REFRESH TOKEN

`routers/auth.py`:

- Genera un nuevo access_token usando el refresh_token

```
curl -k -X POST "https://localhost:8443/auth/refresh" \
  -H "Content-Type: application/json" \
  -d '{
    "refresh_token": "<REFRESH_TOKEN>"
  }'
```

RESPUESTA:

```
access_token: str
token_type: str = "bearer"
```

### LOGOUT

`routers/auth.py`:

- Invalida el refresh_token actual

```
curl -k -X POST "https://localhost:8443/auth/logout" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "refresh_token": "<REFRESH_TOKEN>"
  }'
```

RESPUESTA:

```
message: "Logged out successfully"
```

---

## USUARIOS

### OBTENER PERFIL PROPIO

`routers/users.py`:

- Obtiene la información del usuario autenticado

```
curl -k -X GET "https://localhost:8443/users/me" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json"
```

RESPUESTA:

```
id: int
username: str
email: str
full_name: str
bio: Optional[str] = None
avatar_url: Optional[str] = None
is_active: bool
is_verified: bool
created_at: datetime
updated_at: datetime
last_login: Optional[datetime] = None
```

### ACTUALIZAR INFO

`routers/users.py`:

- Actualiza datos del usuario que hace la petición

```
curl -k -X PUT "https://localhost:8443/users/me" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "full_name": "NUEVO_NOMBRE",
    "bio": "NUEVA_BIO",
    "avatar_url": "NUEVA_URL_AVATAR"
  }'
```

RESPUESTA:

```
id: int
username: str
email: str
full_name: str
bio: Optional[str] = None
avatar_url: Optional[str] = None
is_active: bool
is_verified: bool
created_at: datetime
updated_at: datetime
last_login: Optional[datetime] = None
```

### OBTENER EQUIPOS DEL USUARIO

`routers/users.py`:

- Obtiene todos los equipos a los que pertenece el usuario autenticado

```
curl -k -X GET "https://localhost:8443/users/me/teams" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json"
```

RESPUESTA (lista):

```
[
  {
    id: int
    name: str
    description: Optional[str] = None
    created_at: datetime
    updated_at: datetime
    owner_id: int
  }
]
```

---

## EQUIPOS

### CREACION DE EQUIPO

`routers/teams.py`:

- Verifica que el nombre no existe
- Crea el equipo con la estructura de `models.py`
- El dueño es el usuario que lo está creando

```
curl -k -X POST "https://localhost:8443/teams/create" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Developers",
    "description": "Equipo de desarrollo"
  }'
```

RESPUESTA:

```
id: int
name: str
description: Optional[str] = None
created_at: datetime
updated_at: datetime
owner_id: int
```

### UNIRSE A UN EQUIPO

`routers/teams.py`:

- Verifica que no pertenezcas a ese equipo

```
curl -k -X POST "https://localhost:8443/teams/join/<ID_DEL_EQUIPO>" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json"
```

RESPUESTA:

```
id: int
name: str
description: Optional[str] = None
created_at: datetime
updated_at: datetime
owner_id: int
```

### OBTENER MIEMBROS DE UN EQUIPO

`routers/teams.py`:

- Verifica que la persona que está haciendo la petición pertenezca al equipo

```
curl -k -X GET "https://localhost:8443/teams/<ID_DEL_EQUIPO>/members" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json"
```

RESPUESTA (lista):

```
[
  {
    id: int
    username: str
    email: str
    full_name: str
    bio: Optional[str] = None
    avatar_url: Optional[str] = None
    is_active: bool
    is_verified: bool
    created_at: datetime
    updated_at: datetime
    last_login: Optional[datetime] = None
  }
]
```

### OBTENER TODOS LOS EQUIPOS

`routers/teams.py`:

- Lista todos los equipos disponibles

```
curl -k -X GET "https://localhost:8443/teams/" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json"
```

RESPUESTA (lista):

```
[
  {
    id: int
    name: str
    description: Optional[str] = None
    created_at: datetime
    updated_at: datetime
    owner_id: int
  }
]
```

### OBTENER UN EQUIPO

`routers/teams.py`:

- Obtiene información de un equipo específico

```
curl -k -X GET "https://localhost:8443/teams/<ID_DEL_EQUIPO>" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json"
```

RESPUESTA:

```
id: int
name: str
description: Optional[str] = None
created_at: datetime
updated_at: datetime
owner_id: int
```

### SALIR DE UN EQUIPO

`routers/teams.py`:

- El usuario abandona el equipo
- No puede ser el owner del equipo

```
curl -k -X DELETE "https://localhost:8443/teams/<ID_DEL_EQUIPO>/leave" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json"
```

RESPUESTA:

```
message: "Successfully left the team"
```

### ELIMINAR EQUIPO

`routers/teams.py`:

- Solo el owner puede eliminar el equipo

```
curl -k -X DELETE "https://localhost:8443/teams/<ID_DEL_EQUIPO>" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json"
```

RESPUESTA:

```
message: "Team deleted successfully"
```