# Configuración de Seguridad - Sistema Stock

## Resumen de Mejoras de Seguridad Aplicadas

### Cambios Realizados

#### 1. Consolidación de Usuarios
- **Antes:** 3 usuarios (flavio, marisa - ADMIN; cajero - VENDEDOR) con contraseñas débiles
- **Después:** 1 usuario único `admin` con rol ADMIN
- **Contraseña:** Ahora se obtiene de variable de entorno `ADMIN_PASSWORD` (no hardcodeada)

#### 2. JWT Mejorado
- **Expiración:** Reducida de 10 horas a 4 horas (mejor seguridad)
- **Roles:** Ahora se extraen correctamente del token y se asignan como authorities
- **Filtro JWT:** Extrae el rol del token y crea authorities `ROLE_ADMIN`

#### 3. CORS Restringido
- **Antes:** Permitía cualquier origen (`*`)
- **Después:** Solo permite:
  - `https://lenceria-stock.netlify.app/` (frontend Netlify)
  - `https://sistema-negocio-lenceria-api.onrender.com` (API en Render)
- **Credenciales:** Habilitado para soportar cookies/tokens

#### 4. Limpieza de Código
- Eliminado `@CrossOrigin(origins = "*")` del AuthController (ya está configurado globalmente)

---

## Variables de Entorno Requeridas

### Render (Backend)
Debes configurar estas variables en tu panel de Render:

```
DB_URL=tu_url_de_supabase
DB_USER=tu_usuario
DB_PASSWORD=tu_contraseña
JWT_SECRET=tu_secreto_jwt_de_al_menos_32_caracteres
ADMIN_PASSWORD=contraseña_segura_para_admin
```

#### Generar Contraseña Segura para ADMIN_PASSWORD
Ejecuta en tu terminal:
```bash
openssl rand -base64 12 | tr -d '=+/' | cut -c1-16
```

O usa una contraseña personalizada de al menos 16 caracteres con:
- Al menos una mayúscula
- Al menos una minúscula
- Al menos un número
- Al menos un símbolo especial

Ejemplo: `Lencer!a2024$Adm`

### Netlify (Frontend)
No se requieren cambios en variables de entorno para el frontend.

---

## Usuario de Acceso

Después del despliegue:
- **Usuario:** `admin`
- **Contraseña:** La que configuraste en `ADMIN_PASSWORD`

---

## Notas Importantes

1. **Antes de desplegar:** Asegúrate de configurar `ADMIN_PASSWORD` en las variables de entorno de Render
2. **Usuarios antiguos:** Los usuarios `flavio`, `marisa` y `cajero` ya no se crearán automáticamente
3. **Base de datos:** Si ya existen usuarios antiguos en la BD, deberás eliminarlos manualmente o reiniciar la tabla
4. **Token JWT:** Ahora expira en 4 horas. Los usuarios deberán volver a iniciar sesión después de ese tiempo

---

## Instrucciones para Despliegue

1. Configura la variable `ADMIN_PASSWORD` en Render
2. Haz commit y push de estos cambios
3. Render desplegará automáticamente
4. Prueba el login con:
   - Usuario: `admin`
   - Contraseña: la que configuraste

---

## Seguridad Adicional Recomendada (Futuro)

- [ ] Implementar rate limiting en login (previene fuerza bruta)
- [ ] Añadir endpoint para cambio de contraseña
- [ ] Implementar refresh tokens
- [ ] Añadir logging de intentos fallidos
- [ ] Considerar implementar 2FA para usuarios admin
