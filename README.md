# Sistema de Gestión de Stock y Ventas

Sistema completo para la gestión de inventario, ventas, compras y gastos de una tienda de lencería. Desarrollado con Spring Boot y desplegado en la nube.

## 📋 Descripción

Este sistema permite:
- **Gestión de inventario**: Artículos, variantes (talla/color), stock en tiempo real
- **Control de ventas**: Registro de ventas con múltiples ítems, descuento automático de stock
- **Gestión de compras**: Registro de compras con incremento de stock
- **Movimientos de stock**: Trazabilidad completa de entradas y salidas
- **Reportes mensuales**: Balance de ingresos, egresos y utilidad neta
- **Seguridad JWT**: Autenticación con tokens

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 21 | Lenguaje principal |
| Spring Boot | 3.4.3 | Framework backend |
| Spring Security | 6.x | Autenticación y autorización |
| JWT (jjwt) | 0.11.5 | Tokens de seguridad |
| Spring Data JPA | 3.x | Acceso a datos |
| PostgreSQL | 14+ | Base de datos producción |
| H2 | 2.x | Base de datos tests |
| Maven | 3.x | Gestión de dependencias |

## 🏗️ Arquitectura

El proyecto sigue una arquitectura **por capas** (Layered Architecture):

```
com.lenceria.sistema_stock/
├── config/          # Configuración (Seguridad, DataSeeder)
├── controllers/     # REST Controllers (API Endpoints)
├── dtos/           # Data Transfer Objects (Validaciones)
├── entities/       # Entidades JPA (Modelo de datos)
├── repositories/   # Repositorios Spring Data JPA
├── security/       # JWT Filter y Utilidades
└── services/       # Lógica de negocio
```

### Flujo de datos
```
Request → Controller → DTO → Service → Repository → Entity → Database
                    ↓
            JWT Security Filter
```

## 📁 Estructura de Carpetas

```
sistema-stock/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/lenceria/sistema_stock/
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── DataSeeder.java
│   │   │       ├── controllers/
│   │   │       │   ├── ArticleController.java
│   │   │       │   ├── SaleController.java
│   │   │       │   ├── PurchaseController.java
│   │   │       │   └── ... (otros controllers)
│   │   │       ├── dtos/
│   │   │       │   ├── ArticleDTO.java
│   │   │       │   ├── SaleRequestDTO.java
│   │   │       │   └── ... (otros DTOs)
│   │   │       ├── entities/
│   │   │       │   ├── Article.java
│   │   │       │   ├── Variant.java
│   │   │       │   ├── Sale.java
│   │   │       │   ├── Purchase.java
│   │   │       │   └── ... (otras entidades)
│   │   │       ├── repositories/
│   │   │       │   ├── ArticleRepository.java
│   │   │       │   ├── SaleRepository.java
│   │   │       │   └── ... (otros repositorios)
│   │   │       ├── security/
│   │   │       │   ├── JwtFilter.java
│   │   │       │   └── JwtUtil.java
│   │   │       └── services/
│   │   │           ├── ArticleService.java
│   │   │           ├── SaleService.java
│   │   │           └── ... (otros servicios)
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/
│       │   └── com/lenceria/sistema_stock/
│       │       └── services/
│       │           ├── StockMovementServiceTest.java
│       │           ├── SaleServiceTest.java
│       │           └── PurchaseServiceTest.java
│       └── resources/
│           └── application-test.properties
├── pom.xml
├── .env (no commiteado)
├── .gitignore
└── README.md
```

## 🚀 Configuración Inicial

### Requisitos previos
- Java 21
- Maven 3.8+
- PostgreSQL (para producción)

### 1. Clonar el repositorio
```bash
git clone <url-del-repositorio>
cd sistema-stock
```

### 2. Configurar variables de entorno
Crear archivo `.env` en la raíz:

```bash
# Base de datos (PostgreSQL)
DB_URL=localhost:5432/sistema_stock
DB_USER=tu_usuario
DB_PASSWORD=tu_password

# JWT Secret (generar una clave segura de 32+ caracteres)
JWT_SECRET=tu-clave-secreta-muy-larga-para-jwt-32-bytes-minimo
```

### 3. Compilar y ejecutar
```bash
# Compilar
mvn clean compile

# Ejecutar con perfil de desarrollo
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 🧪 Tests

### Ejecutar todos los tests
```bash
mvn test
```

### Ejecutar un test específico
```bash
mvn test -Dtest=StockMovementServiceTest
```

### Cobertura de tests
Los tests actuales cubren:
- **StockMovementService**: 9 escenarios (entrada/salida, validaciones, excepciones)
- **SaleService**: 8 escenarios (ventas completas, cálculos, validaciones)
- **PurchaseService**: 8 escenarios (compras, costos personalizados, validaciones)

**Tecnologías de testing:**
- JUnit 5
- Mockito
- Spring Boot Test

## 🔐 Seguridad

### Autenticación JWT
1. **Login**: `POST /api/auth/login`
   - Request: `{ "username": "admin", "password": "1234" }`
   - Response: `{ "token": "eyJhbG..." }`

2. **Uso del token**: Incluir en header de todas las peticiones:
   ```
   Authorization: Bearer eyJhbG...
   ```

### Configuración de seguridad
- JWT Secret configurado via variables de entorno
- Tokens válidos por 10 horas
- Rutas protegidas excepto `/api/auth/login`
- CORS habilitado para desarrollo

## 📡 API Endpoints

### Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Iniciar sesión |

### Artículos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/articulos` | Listar todos |
| GET | `/api/articulos/{id}` | Obtener por ID |
| POST | `/api/articulos` | Crear artículo |
| PUT | `/api/articulos/{id}` | Actualizar |
| DELETE | `/api/articulos/{id}` | Eliminar (soft delete) |

### Ventas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/ventas` | Listar ventas (filtros: ?anio=2024&mes=3) |
| POST | `/api/ventas` | Registrar nueva venta |

### Compras
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/compras` | Listar compras |
| POST | `/api/compras` | Registrar nueva compra |

### Stock
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/stock` | Listar movimientos |
| POST | `/api/stock` | Registrar movimiento manual |

### Reportes
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/reportes/balance?anio=2024&mes=3` | Balance mensual |

## 🎯 Modelo de Datos

### Entidades principales

```
Article (1) ---- (*) Variant (*) ---- (1) StockMovement
   |                              |
   |                              |
(*)                              (*) SaleDetail (*) ---- (1) Sale
ArticleCategory                                       |
                                                      |
                                                 Purchase
                                                      |
                                                 PurchaseDetail

Expense (*) ---- (1) ExpenseCategory

User (autenticación)
```

### Campos importantes
- **Variant.stock**: Stock actual (Integer)
- **StockMovement**: Trazabilidad completa (tipo, cantidad, fecha, motivo)
- **SaleDetail.unitPrice**: Precio congelado al momento de la venta
- **PurchaseDetail.unitPrice**: Costo unitario (puede ser personalizado)

## 📝 Buenas Prácticas Implementadas

### ✅ Seguridad
- Variables de entorno para secrets (no hardcodeados)
- JWT para autenticación stateless
- Contraseñas hasheadas con BCrypt
- `.gitignore` excluye `.env`

### ✅ Código
- Arquitectura por capas clara
- Inyección de dependencias (constructor)
- Transacciones en operaciones críticas (`@Transactional`)
- Soft delete (campo `active`)
- DTOs para validaciones
- Manejo de excepciones en español

### ✅ Testing
- Tests unitarios con Mockito
- Cobertura de casos de éxito y error
- H2 para tests (aislamiento)
- @BeforeEach para setup

## 🚀 Despliegue en Producción

### Configuración requerida
1. Variables de entorno configuradas en el servidor
2. Base de datos PostgreSQL accesible
3. Puerto 8080 disponible (o configurar `server.port`)

### Construcción del JAR
```bash
mvn clean package
```

### Ejecutar JAR
```bash
java -jar target/sistema-stock-0.0.1-SNAPSHOT.jar
```

## 🐛 Troubleshooting

### Error: "No hay suficiente stock"
- Verificar stock actual del artículo
- El sistema no permite stock negativo

### Error: "Token expirado"
- El token JWT dura 10 horas
- Solicitar nuevo token en `/api/auth/login`

### Error de conexión a base de datos
- Verificar variables `DB_URL`, `DB_USER`, `DB_PASSWORD`
- Asegurar que PostgreSQL esté corriendo
- Verificar red/firewall

## 📈 Próximas Mejoras

- [ ] Tests de integración con `@DataJpaTest`
- [ ] Tests de controladores con `@WebMvcTest`
- [ ] Documentación Swagger/OpenAPI
- [ ] Docker para desarrollo
- [ ] Perfiles de Spring (dev/test/prod)
- [ ] Caché con Redis
- [ ] Métricas personalizadas con Actuator

## ⚡ Comandos Importantes
### Antes de cada commit/deploy
Para compilar y empaquetar la aplicación (requerido por Render para que funcione más rápido):

```bash
mvn clean package -DskipTests
Qué hace:
- mvn clean - Limpia archivos compilados anteriores
- package - Compila y genera el JAR ejecutable
- -DskipTests - Salta los tests (para acelerar el build en Render)
Esto genera el archivo JAR en target/ que Render usa para desplegar la aplicación.
Recordatorio: Ejecutar este comando siempre antes de hacer git commit y push.

## 👨‍💻 Autor

**Desarrollador**: Gonzalo Cid
**Proyecto**: Primer sistema con Spring Boot
**Año**: 2026

---

## 📄 Licencia

Este proyecto es de uso personal/educativo.

---

**¿Necesitas ayuda?** Abre un issue en el repositorio o revisa la documentación de Spring Boot.
