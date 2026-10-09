# RecetaYa — Microservicio de Inventario

API REST desarrollada con Java 21, Spring Boot y MySQL 8.4 para gestionar medicamentos, sucursales, stock y reservas.

## 1. Requisitos

- Java JDK 21
- MySQL Server 8.4
- Git
- Postman

Maven Wrapper está incluido en el proyecto.

## 2. Clonar el repositorio

```powershell
git clone https://github.com/PabloCanelos/recetaya-inventario.git
cd recetaya-inventario
```

## 3. Configurar MySQL

Crear la base de datos:

```sql
CREATE DATABASE IF NOT EXISTS db_inventario;
```

Configurar las credenciales en PowerShell:

```powershell
$env:DB_USERNAME = "inventario_app"
$env:DB_PASSWORD = Read-Host "Contraseña MySQL"
```

El usuario debe existir y tener permisos sobre `db_inventario`.

## 4. Ejecutar el microservicio

```powershell
.\mvnw.cmd spring-boot:run
```

Servidor: `http://localhost:8082`

## 5. Probar con Postman

| Método | Endpoint | Función |
|---|---|---|
| GET | /api/medicamentos | Listar medicamentos |
| POST | /api/medicamentos | Crear medicamento |
| PUT | /api/medicamentos/{id} | Actualizar medicamento |
| DELETE | /api/medicamentos/{id} | Eliminar medicamento |
| GET | /api/stock | Consultar stock |
| POST | /api/reservas | Crear reserva |

Las demás operaciones están implementadas en sus respectivos controladores.

## 6. Generar JAR

```powershell
.\mvnw.cmd clean package
```

El archivo ejecutable se genera en `target/`.

Para ejecutarlo:

```powershell
java -jar target\ms-inventario-0.0.1-SNAPSHOT.jar
```

## Arquitectura

Controller → Service → Repository → MySQL

Se utiliza Spring Data JPA para la persistencia de datos.

**Alcance:** CRUD de medicamentos y sucursales, gestión de stock, reservas y dispensación. La integración automática con otros microservicios no está implementada.
