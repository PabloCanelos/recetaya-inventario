# RecetaYa — Microservicio de Inventario

Microservicio REST que administra medicamentos, sucursales, stock y reservas de medicamentos para el sistema RecetaYa.

## 1. Requisitos

Antes de comenzar, necesitas instalar:

- Java JDK 21.
- MySQL Server 8.4.
- Git.
- Postman para probar la API.

No necesitas instalar Maven por separado, porque el proyecto incluye Maven Wrapper.

## 2. Descargar el proyecto

Abre PowerShell y ejecuta:

```powershell
git clone https://github.com/PabloCanelos/recetaya-inventario.git
cd recetaya-inventario
```

## 3. Crear la base de datos

Abre MySQL Workbench, conéctate a tu servidor MySQL y ejecuta:

```sql
CREATE DATABASE IF NOT EXISTS db_inventario;
```

La base de datos se llama `db_inventario` y utiliza el puerto `3306`.

## 4. Configurar la conexión

En PowerShell, configura las credenciales de tu usuario MySQL:

```powershell
$env:DB_USERNAME = "inventario_app"
$env:DB_PASSWORD = Read-Host "Ingresa tu contraseña de MySQL"
```

Escribe la contraseña cuando PowerShell la solicite.

El usuario debe existir en MySQL y tener permisos sobre `db_inventario`. Si no tienes ese usuario configurado, utiliza uno que disponga de los permisos necesarios.

No publiques contraseñas en GitHub.

## 5. Ejecutar el microservicio

Desde la carpeta `recetaya-inventario`, ejecuta:

```powershell
.\mvnw.cmd spring-boot:run
```

Si la configuración es correcta, Spring Boot iniciará el microservicio en:

http://localhost:8082

Hibernate crea o actualiza las tablas automáticamente al iniciar la aplicación, de acuerdo con la configuración del proyecto.

Mantén abierta la terminal mientras utilizas la API.

## 6. Probar la API con Postman

Utiliza Postman para enviar solicitudes a:

`http://localhost:8082`

Principales operaciones disponibles:

| Método | Endpoint | Función |
|---|---|---|
| GET | `/api/medicamentos` | Listar medicamentos |
| GET | `/api/medicamentos/{idMedicamento}` | Buscar medicamento |
| POST | `/api/medicamentos` | Registrar medicamento |
| PUT | `/api/medicamentos/{idMedicamento}` | Actualizar medicamento |
| DELETE | `/api/medicamentos/{idMedicamento}` | Eliminar medicamento |
| GET | `/api/sucursales` | Listar sucursales |
| POST | `/api/sucursales` | Registrar sucursal |
| GET | `/api/stock` | Consultar stock |
| GET | `/api/stock/{idStock}` | Buscar stock por ID |
| GET | `/api/stock/consultar?idMedicamento=1&idSucursal=1` | Consultar stock por medicamento y sucursal |
| POST | `/api/stock` | Registrar stock |
| GET | `/api/reservas` | Listar reservas |
| GET | `/api/reservas/{idReserva}` | Buscar reserva |
| GET | `/api/reservas/receta/{idReceta}` | Buscar reserva por receta |
| POST | `/api/reservas` | Crear una reserva |
| PUT | `/api/reservas/{idReserva}/cancelar` | Cancelar una reserva |

Las operaciones de actualización y eliminación de sucursales también están disponibles mediante sus endpoints `PUT` y `DELETE`, respectivamente.

### Ejemplo: registrar un medicamento

En Postman, selecciona `POST` e introduce:

`http://localhost:8082/api/medicamentos`

En **Body → raw → JSON**, escribe:

```json
{
  "nombre": "Paracetamol",
  "descripcion": "Analgésico y antipirético",
  "activo": true
}
```

Pulsa **Send** para enviar la solicitud.

### Ejemplo: reservar medicamento

Selecciona `POST` e introduce:

`http://localhost:8082/api/reservas`

En **Body → raw → JSON**, escribe:

```json
{
  "idReceta": 106,
  "idSucursal": 1,
  "idMedicamento": 1,
  "cantidad": 3
}
```

Los identificadores deben corresponder a registros existentes y debe haber stock suficiente. La reserva modifica las cantidades disponibles y reservadas.

## 7. Generar el archivo JAR

Para compilar y empaquetar el proyecto, ejecuta:

```powershell
.\mvnw.cmd clean package
```

Si todo funciona correctamente, Maven mostrará:

`BUILD SUCCESS`

El archivo ejecutable se generará en la carpeta `target`.

Para ejecutarlo, utiliza:

```powershell
java -jar target\ms-inventario-0.0.1-SNAPSHOT.jar
```

La conexión a MySQL debe estar configurada antes de iniciar la aplicación.

## 8. Arquitectura

El microservicio utiliza una arquitectura por capas:

- **Controller:** recibe las solicitudes HTTP.
- **Service:** ejecuta las reglas de negocio.
- **Repository:** accede a la base de datos.
- **Entity:** representa las entidades almacenadas.
- **DTO:** define los datos enviados y recibidos por la API.

Spring Data JPA e Hibernate gestionan la persistencia en MySQL.

## 9. Respuestas HTTP

- `200 OK`: solicitud procesada correctamente.
- `201 Created`: recurso creado.
- `400 Bad Request`: datos inválidos o regla de negocio incumplida.
- `404 Not Found`: recurso inexistente.
- `409 Conflict`: conflicto con el estado actual de una operación.

## 10. Alcance

El microservicio administra el catálogo de medicamentos, las sucursales, el stock por sucursal y las reservas, incluyendo su cancelación y el registro de la dispensación.

La integración automática con otros microservicios y los componentes AWS SQS y Lambda no se considera implementada por esta documentación.

Este proyecto forma parte de RecetaYa y utiliza MySQL Server 8.4 para la persistencia de datos.