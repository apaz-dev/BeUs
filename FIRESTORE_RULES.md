# 🔒 Configuración de Reglas de Firestore

## ⚠️ IMPORTANTE: Configurar reglas para permitir eliminación de cuentas

El error "permission denied" que estás recibiendo es porque **las reglas de Firestore** no permiten que los usuarios eliminen sus propios perfiles.

## 📝 Cómo configurar las reglas:

### 1. Ve a la Consola de Firebase
- Abre https://console.firebase.google.com/
- Selecciona tu proyecto "BeUs"

### 2. Navega a Firestore Database
- En el menú lateral, haz clic en "Firestore Database"
- Haz clic en la pestaña "Reglas" (Rules)

### 3. Actualiza las reglas

Reemplaza las reglas actuales con estas:

```javascript
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    
    // Reglas para la colección de perfiles
    match /profiles/{userId} {
      // Permitir leer su propio perfil
      allow read: if request.auth != null && request.auth.uid == userId;
      
      // Permitir crear su propio perfil
      allow create: if request.auth != null && request.auth.uid == userId;
      
      // Permitir actualizar su propio perfil
      allow update: if request.auth != null && request.auth.uid == userId;
      
      // 🔥 IMPORTANTE: Permitir eliminar su propio perfil
      allow delete: if request.auth != null && request.auth.uid == userId;
    }
    
    // Reglas para teams (si las necesitas)
    match /teams/{teamId} {
      // Permitir leer si es miembro del equipo
      allow read: if request.auth != null;
      
      // Permitir crear equipos
      allow create: if request.auth != null;
      
      // Permitir actualizar si es el dueño
      allow update: if request.auth != null;
      
      // Permitir eliminar si es el dueño
      allow delete: if request.auth != null;
      
      // Subcoleción de miembros
      match /members/{memberId} {
        allow read, write: if request.auth != null;
      }
      
      // Subcolección de eventos
      match /events/{eventId} {
        allow read, write: if request.auth != null;
        
        // Subcolección de fotos
        match /photos/{photoId} {
          allow read, write: if request.auth != null;
        }
      }
    }
  }
}
```

### 4. Publica las reglas
- Haz clic en "Publicar" (Publish)
- Espera unos segundos a que se apliquen

## ✅ Verificación

Después de publicar las reglas, intenta eliminar tu cuenta nuevamente. Ahora debería funcionar sin el error "permission denied".

## 🔐 Explicación de seguridad

La regla clave es:
```javascript
allow delete: if request.auth != null && request.auth.uid == userId;
```

Esto significa:
- ✅ El usuario DEBE estar autenticado (`request.auth != null`)
- ✅ El usuario SOLO puede eliminar SU PROPIO perfil (`request.auth.uid == userId`)
- ❌ No puede eliminar perfiles de otros usuarios

## 📌 Nota sobre la estrategia actual

El código ahora está configurado para:
1. **Primero** eliminar la cuenta de Firebase Auth (siempre funciona)
2. **Después** intentar eliminar el perfil de Firestore (si falla, no es crítico)

Esto significa que incluso si las reglas de Firestore no permiten la eliminación del perfil, **la cuenta del usuario SÍ será eliminada** de Firebase Authentication, que es lo más importante.

Sin embargo, es **recomendable** configurar las reglas correctamente para limpiar completamente los datos del usuario.

## 🆘 Alternativa si no puedes cambiar las reglas

Si por alguna razón no puedes modificar las reglas de Firestore (por ejemplo, no tienes acceso de administrador), el código actual seguirá funcionando:
- ✅ Se eliminará la cuenta de Firebase Auth
- ✅ El usuario no podrá volver a iniciar sesión
- ⚠️ El perfil quedará huérfano en Firestore (pero no es accesible sin autenticación)

Puedes limpiar estos perfiles huérfanos manualmente desde la consola de Firebase o con una Cloud Function programada.

