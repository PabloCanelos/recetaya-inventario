# RecetaYa — Microservicio de Inventario

Microservicio REST desarrollado con **Java 21**, **Spring Boot**, **Spring Data JPA/Hibernate**, **Maven** y **MySQL 8.4**. Administra medicamentos, sucursales, existencias y reservas de medicamentos para el sistema RecetaYa.

> **Alcance de este repositorio:** únicamente el microservicio de Inventario. Los microservicios de Recetas, Autenticación y Usuarios, y Dispensación se mantienen separados.

## Funcionalidades implementadas

- CRUD de medicamentos y sucursales.
- Registro, consulta, actualización y eliminación de stock por medicamento y sucursal.
- Consulta de disponibilidad de stock.
- Reserva de unidades para una receta, con validación de existencias y prevención de reservas duplicadas.
- Cancelación de reservas activas, devolviendo las unidades al stock disponible.
- Confirmación de dispensación, descontando las unidades reservadas **sin descontar nuevamente** las disponibles.
- Persistencia transaccional con JPA y bloqueos pesimistas para operaciones críticas.
- Respuestas de error HTTP para solicitudes inválidas, recursos inexistentes y conflictos de estado.

**Estados de reserva:** `ACTIVA`, `CANCELADA` y `DISPENSADA`.

**Alcance actual:** cada solicitud de reserva contiene **un medicamento y una cantidad**. La integración automática con otros microservicios y con AWS SQS/Lambda no está implementada ni validada en este repositorio.

## Requisitos previos

- JDK **21**.
- MySQL Server **8.4**.
- Git.
- Postman (recomendado para probar la API).
- No es necesario instalar Maven globalmente: el proyecto incluye Maven Wrapper (`mvnw.cmd` en Windows y `mvnw` en Linux/macOS).

## Clonar el repositorio

```bash
git clone https://github.com/PabloCanelos/recetaya-inventario.git
cd recetaya-inventario
```

## Base de datos

El microservicio utiliza una base de datos independiente llamada `db_inventario`, en MySQL local (puerto `3306`).

Crear la base de datos, si todavía no existe:

```sql
CREATE DATABASE IF NOT EXISTS db_inventario;
```

Si todavía no tienes un usuario de aplicación, puedes crearlo desde MySQL Workbench con una cuenta administradora (reemplaza `TU_PASSWORD_SEGURA` por una contraseña propia y no la subas a GitHub):

```sql
CREATE USER IF NOT EXISTS 'inventario_app'@'localhost' IDENTIFIED BY 'TU_PASSWORD_SEGURA';
GRANT ALL PRIVILEGES ON db_inventario.* TO 'inventario_app'@'localhost';
```

Si el usuario `inventario_app` ya existe, **no necesitas crearlo nuevamente**; utiliza su contraseña actual. La cuenta MySQL configurada debe tener permisos sobre `db_inventario`. El proyecto utiliza entidades JPA y `spring.jpa.hibernate.ddl-auto=update`, por lo que Hibernate puede crear o actualizar las tablas al iniciar. Para un entorno de producción se recomienda usar migraciones versionadas en lugar de `update`.

Tablas principales: `medicamento`, `sucursal`, `stock`, `reserva_stock` y `detalle_reserva` (los nombres físicos deben verificarse con el esquema generado por Hibernate).

## Configuración

La configuración utilizada durante las pruebas locales es:

```properties
spring.application.name=ms-inventario
server.port=8082
spring.datasource.url=jdbc:mysql://localhost:3306/db_inventario
spring.datasource.username=${DB_USERNAME:inventario_app}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Configura `DB_PASSWORD` antes de ejecutar la aplicación. `DB_USERNAME` es opcional si tu usuario de MySQL se llama `inventario_app`; de lo contrario, debes configurarlo también.

**PowerShell (Windows):**

```powershell
$env:DB_USERNAME = "inventario_app"
$env:DB_PASSWORD = Read-Host "Contraseña de MySQL"
```

Estas variables se configuran en la misma terminal desde la que se ejecuta la aplicación. **No publiques contraseñas en GitHub**.

## Compilar, probar y ejecutar

En **Windows / PowerShell**:

```powershell
# Limpiar artefactos anteriores
.\mvnw.cmd clean

# Compilar y ejecutar las pruebas configuradas; generar el JAR
.\mvnw.cmd clean package

# Ejecutar directamente con Spring Boot
.\mvnw.cmd spring-boot:run
```

También puedes ejecutar el artefacto generado (en lugar de `spring-boot:run`):

```powershell
java -jar .\target\ms-inventario-0.0.1-SNAPSHOT.jar
```

Para ejecutar únicamente las pruebas automatizadas configuradas:

```powershell
.\mvnw.cmd test
```

En **Linux/macOS**, utiliza `./mvnw` en lugar de `.\mvnw.cmd` y configura las variables de entorno en tu shell.

**URL base local:** `http://localhost:8082`

## Endpoints REST

### Medicamentos

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/medicamentos` | Listar medicamentos |
| GET | `/api/medicamentos/{id}` | Consultar medicamento |
| POST | `/api/medicamentos` | Registrar medicamento |
| PUT | `/api/medicamentos/{id}` | Actualizar medicamento |
| DELETE | `/api/medicamentos/{id}` | Eliminar medicamento |

### Sucursales

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/sucursales` | Listar sucursales |
| GET | `/api/sucursales/{id}` | Consultar sucursal |
| POST | `/api/sucursales` | Registrar sucursal |
| PUT | `/api/sucursales/{id}` | Actualizar sucursal |
| DELETE | `/api/sucursales/{id}` | Eliminar sucursal |

### Stock

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/stock` | Listar registros de stock |
| GET | `/api/stock/{idStock}` | Consultar stock por ID |
| GET | `/api/stock/consultar?idMedicamento={idMedicamento}&idSucursal={idSucursal}` | Consultar stock por medicamento y sucursal |
| POST | `/api/stock` | Registrar stock |
| PUT | `/api/stock/{idStock}` | Actualizar cantidad disponible |
| DELETE | `/api/stock/{idStock}` | Eliminar stock, sujeto a reglas de negocio |

### Reservas

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/reservas` | Listar reservas |
| GET | `/api/reservas/{idReserva}` | Consultar reserva |
| GET | `/api/reservas/receta/{idReceta}` | Consultar reserva por receta |
| POST | `/api/reservas` | Crear reserva de medicamento |
| PUT | `/api/reservas/{idReserva}/cancelar` | Cancelar reserva activa |
| PUT | `/api/reservas/{idReserva}/dispensar` | Confirmar dispensación de reserva activa |

Los endpoints de cancelación y dispensación **no requieren cuerpo JSON**.

## Ejemplos para Postman

Selecciona **Body → raw → JSON** para las solicitudes que requieren cuerpo y utiliza `Content-Type: application/json`.

**Crear medicamento:**

```http
POST http://localhost:8082/api/medicamentos
```

```json
{
  "nombre": "Paracetamol",
  "descripcion": "Analgésico y antipirético",
  "activo": true
}
```

**Registrar stock** (requiere IDs de medicamento y sucursal existentes):

```http
POST http://localhost:8082/api/stock
```

```json
{
  "idMedicamento": 1,
  "idSucursal": 1,
  "cantidad": 20
}
```

**Reservar medicamento** (requiere stock disponible y un `idReceta` que no tenga reserva previa):

```http
POST http://localhost:8082/api/reservas
```

```json
{
  "idReceta": 2001,
  "idSucursal": 1,
  "idMedicamento": 1,
  "cantidad": 3
}
```

Utiliza un `idReceta` nuevo en cada prueba: si ya existe una reserva para ese identificador, el sistema rechazará el duplicado. La respuesta contiene `idReserva`, `estado`, `fechaReserva`, datos de sucursal y detalles del medicamento. Usa el `idReserva` devuelto para las operaciones siguientes.

**Cancelar una reserva activa:**

```http
PUT http://localhost:8082/api/reservas/{idReserva}/cancelar
```

**Confirmar dispensación de una reserva activa:**

```http
PUT http://localhost:8082/api/reservas/{idReserva}/dispensar
```

**Importante:** cancelar y dispensar son acciones alternativas sobre una reserva activa. No es posible cancelar una reserva ya dispensada ni dispensar una reserva cancelada.

## Reglas de stock

- Al reservar `N` unidades: `cantidadDisponible` disminuye en `N` y `cantidadReservada` aumenta en `N`.
- Al cancelar una reserva activa: `cantidadDisponible` aumenta en `N` y `cantidadReservada` disminuye en `N`.
- Al dispensar una reserva activa: **solo** `cantidadReservada` disminuye en `N`, porque las unidades disponibles ya se descontaron al reservar.
- No se permiten cantidades de reserva iguales o inferiores a cero ni reservas sin stock suficiente.
- Las operaciones críticas usan transacciones y bloqueos para reducir el riesgo de modificaciones simultáneas inconsistentes.

## Códigos HTTP y validaciones

| Código | Uso |
|---|---|
| `200 OK` | Operaciones exitosas comprobadas en Postman |
| `400 Bad Request` | Datos inválidos, por ejemplo una cantidad no positiva o stock insuficiente |
| `404 Not Found` | Recurso inexistente |
| `409 Conflict` | Conflicto de estado o regla de negocio, por ejemplo cancelar una reserva ya dispensada |

Ejemplo de error al intentar cancelar una reserva dispensada:

```json
{
  "error": "Conflict",
  "mensaje": "Solo se pueden cancelar reservas activas",
  "status": 409
}
```

La respuesta real también incluye un campo `timestamp`.

## Estructura del código

```text
src/main/java/com/inventario/
├── controller/   # Endpoints REST
├── dto/          # Objetos de solicitud y respuesta
├── entity/       # Entidades JPA
├── exception/    # Manejo centralizado de errores
├── repository/   # Acceso a datos con Spring Data JPA
└── service/      # Reglas de negocio y transacciones
```

## Secuencia sugerida para demostrar el proyecto desde cero

1. Clonar el repositorio y comprobar `java -version`.
2. Crear `db_inventario` y configurar el usuario MySQL.
3. Configurar las variables de entorno sin mostrar la contraseña en la grabación.
4. Ejecutar ` .\mvnw.cmd clean package ` y mostrar el resultado y el JAR de `target/`.
5. Iniciar la aplicación con `java -jar .\target\ms-inventario-0.0.1-SNAPSHOT.jar`.
6. Probar en Postman un CRUD completo y una reserva con su cambio de stock; mostrar también un error `404` o `409`.
7. Explicar la separación Controller → Service → Repository → Entity y las anotaciones JPA usadas.

Esta secuencia sirve como guía para la demostración técnica de la EP2; el video grupal y la entrega en AVA son responsabilidad del equipo.

## Verificaciones realizadas

Durante el desarrollo se comprobaron con Postman los CRUD de medicamentos, sucursales y stock; la creación y cancelación de reservas; la confirmación de dispensación; y respuestas ante recursos inexistentes, stock insuficiente, reservas duplicadas y conflictos de estado.

También se obtuvo `BUILD SUCCESS` al ejecutar `mvnw.cmd clean package` y se verificó el inicio del JAR generado en el puerto `8082`.

Estas verificaciones corresponden a pruebas locales del microservicio; **no equivalen a una prueba de integración extremo a extremo** con Recetas, Dispensación o componentes AWS.

## Integración con otros microservicios

Inventario conserva `idReceta` como identificador externo de Recetas, sin clave foránea entre bases de datos. La coordinación automática con Recetas y Dispensación, así como cualquier procesamiento con Amazon SQS o AWS Lambda, requiere definir y probar los contratos de integración entre componentes. No forma parte de las funcionalidades verificadas en este repositorio.

## Repositorio

[GitHub — recetaya-inventario](https://github.com/PabloCanelos/recetaya-inventario)
