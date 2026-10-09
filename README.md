RecetaYa — Microservicio de Inventario
Este microservicio administra medicamentos, sucursales, stock y reservas de medicamentos.
1. ¿Qué necesitas instalar?
- Java JDK 21.
- MySQL Server 8.4.
- Git.
2. Descargar el proyecto
Abre una terminal y ejecuta:
git clone https://github.com/PabloCanelos/recetaya-inventario.git
cd recetaya-inventario


3. Preparar la base de datos
Abre MySQL Workbench y crea la base de datos:
CREATE DATABASE db_inventario;


4. Configurar la conexión
Abre src/main/resources/application.properties y configura la conexión con tu usuario y contraseña de MySQL.
No publiques tus credenciales en GitHub.
5. Ejecutar el microservicio
En la terminal, dentro de la carpeta del proyecto, ejecuta:
.\mvnw.cmd spring-boot:run


Si todo está bien configurado, Spring Boot iniciará el servidor en el puerto 8082.
6. Probar la API
Abre Postman y prueba los endpoints documentados en la sección de pruebas.
