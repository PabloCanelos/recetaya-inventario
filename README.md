# recetaya-inventario
# RecetaYa — Inventario

Microservicio responsable del catálogo de medicamentos, las sucursales, el stock y las reservas de medicamentos para las recetas.

## Contexto del proyecto

RecetaYa tiene cuatro microservicios Spring Boot: Autenticación y Usuarios, Recetas, Inventario y Dispensación. Este repositorio contiene únicamente **Inventario** y su base de datos.

La reserva ocurre después de emitir la receta. Recetas envía una solicitud a **Amazon SQS**, **AWS Lambda** la procesa y llama a Inventario para verificar y reservar el stock. SQS y Lambda son componentes de AWS; no son clases de entidad ni microservicios adicionales de este repositorio.

## Configuración acordada

| Concepto | Valor |
|---|---|
| Motor de base de datos | MySQL Server 8.4 |
| Base de datos propia | `db_inventario` |
| Puerto local del microservicio | `8083` |
| Puerto reservado para la entrada local | `8080` |

El puerto `8083` corresponde a Spring Boot. La conexión a MySQL se configura por separado.

## Responsabilidades

Este microservicio debe:

1. Mantener el catálogo de medicamentos y las sucursales.
2. Registrar el stock de cada medicamento en cada sucursal.
3. Verificar si hay unidades disponibles para **todos** los medicamentos de una receta.
4. Reservar las unidades cuando haya stock suficiente.
5. Comunicar a Recetas si la reserva se realizó o fue rechazada.
6. Confirmar el consumo de las unidades reservadas cuando Dispensación registre la entrega.

El diagrama entregado muestra `GET /medicamentos`, `GET /sucursales/{id}/stock` y `POST /reservas`. La solicitud a `POST /reservas` proviene del procesamiento interno del mensaje; los contratos exactos entre servicios se acordarán antes de integrarlos.

## Clases de entidad y modelo relacional

Crear **cinco clases de entidad**: `Medicamento`, `Sucursal`, `Stock`, `ReservaStock` y `DetalleReserva`. Todas sus tablas pertenecen a `db_inventario`.

### Clase `Medicamento` — tabla `MEDICAMENTO`

| Atributo Java | Tipo Java | Columna MySQL | Tipo MySQL | Restricción |
|---|---|---|---|---|
| `id` | `Integer` | `id_medicamento` | `INT` | Clave primaria, autoincremental |
| `nombre` | `String` | `nombre` | `VARCHAR(150)` | Obligatorio |
| `descripcion` | `String` | `descripcion` | `VARCHAR(500)` | Opcional |
| `activo` | `Boolean` | `activo` | `BOOLEAN` | Obligatorio |

### Clase `Sucursal` — tabla `SUCURSAL`

| Atributo Java | Tipo Java | Columna MySQL | Tipo MySQL | Restricción |
|---|---|---|---|---|
| `id` | `Integer` | `id_sucursal` | `INT` | Clave primaria, autoincremental |
| `nombre` | `String` | `nombre` | `VARCHAR(150)` | Obligatorio |
| `direccion` | `String` | `direccion` | `VARCHAR(255)` | Opcional |

### Clase `Stock` — tabla `STOCK`

| Atributo Java | Tipo Java | Columna MySQL | Tipo MySQL | Restricción |
|---|---|---|---|---|
| `id` | `Integer` | `id_stock` | `INT` | Clave primaria, autoincremental |
| `medicamento` | `Medicamento` | `id_medicamento` | `INT` | Clave foránea obligatoria a `MEDICAMENTO.id_medicamento` |
| `sucursal` | `Sucursal` | `id_sucursal` | `INT` | Clave foránea obligatoria a `SUCURSAL.id_sucursal` |
| `cantidadDisponible` | `Integer` | `cantidad_disponible` | `INT` | Obligatorio; cero o mayor |
| `cantidadReservada` | `Integer` | `cantidad_reservada` | `INT` | Obligatorio; cero o mayor |

Debe existir **una sola fila de `STOCK` por combinación de medicamento y sucursal**: restricción `UNIQUE(id_medicamento, id_sucursal)`.

### Clase `ReservaStock` — tabla `RESERVA_STOCK`

| Atributo Java | Tipo Java | Columna MySQL | Tipo MySQL | Restricción |
|---|---|---|---|---|
| `id` | `Integer` | `id_reserva` | `INT` | Clave primaria, autoincremental |
| `idReceta` | `Integer` | `id_receta` | `INT` | Obligatorio y único; ID externo de Recetas |
| `sucursal` | `Sucursal` | `id_sucursal` | `INT` | Clave foránea obligatoria a `SUCURSAL.id_sucursal` |
| `estado` | `EstadoReserva` | `estado` | `VARCHAR(32)` | Obligatorio; guardar el nombre del estado como texto |
| `fechaReserva` | `LocalDateTime` | `fecha_reserva` | `DATETIME` | Fecha de la reserva; puede quedar vacía si fue rechazada |

`EstadoReserva` es un enum **interno de Inventario**. Para empezar, usar `RESERVADA` y `RECHAZADA`. Son estados de la operación de reserva; no sustituyen los estados de la receta que almacena Recetas.

### Clase `DetalleReserva` — tabla `DETALLE_RESERVA`

| Atributo Java | Tipo Java | Columna MySQL | Tipo MySQL | Restricción |
|---|---|---|---|---|
| `id` | `Integer` | `id_detalle_reserva` | `INT` | Clave primaria, autoincremental |
| `reserva` | `ReservaStock` | `id_reserva` | `INT` | Clave foránea obligatoria a `RESERVA_STOCK.id_reserva` |
| `stock` | `Stock` | `id_stock` | `INT` | Clave foránea obligatoria a `STOCK.id_stock` |
| `cantidad` | `Integer` | `cantidad` | `INT` | Obligatorio y mayor que cero |

Una reserva no debe repetir el mismo stock entre sus detalles: restricción `UNIQUE(id_reserva, id_stock)`.

## Relaciones y reglas de stock

- Un `Medicamento` puede tener stock en varias sucursales.
- Una `Sucursal` puede tener stock de varios medicamentos y recibir varias reservas.
- Una `ReservaStock` contiene uno o más `DetalleReserva`.
- Cada `DetalleReserva` indica qué fila de `Stock` se reservó y cuántas unidades.
- Todos los detalles de una reserva deben corresponder a la sucursal indicada en `ReservaStock`.

`cantidadDisponible` representa las unidades que todavía pueden reservarse. `cantidadReservada` representa unidades ya comprometidas para recetas.

**Ejemplo:** si hay 10 unidades disponibles y se reservan 3, el resultado es 7 disponibles y 3 reservadas. Al confirmar la entrega de esas 3 unidades, las reservadas pasan a 0; **no se vuelven a descontar** de las 7 disponibles.

La reserva de todos los medicamentos de una receta debe realizarse como una sola operación: si falta stock para uno, no se dejan reservados los demás. La verificación y actualización del stock deben hacerse de forma transaccional para evitar que dos solicitudes simultáneas reserven las mismas unidades.

## Comunicación con los otros servicios

- **Desde Lambda:** recibir la solicitud de reserva con `idReceta`, `idSucursal` y medicamentos con sus cantidades.
- **Hacia Recetas:** comunicar el resultado para que **Recetas** cambie el estado de su receta a `STOCK_RESERVADO` o `RESERVA_RECHAZADA`.
- **Desde Dispensación:** recibir la confirmación de consumo de una reserva al realizar la entrega.

`idReceta` pertenece a Recetas. Se guarda como `INT` para identificar la solicitud, **sin clave foránea entre bases de datos**. Su restricción `UNIQUE` ayuda a impedir que un reintento del mismo mensaje reserve las unidades dos veces.

Los formatos de mensajes, las rutas REST internas y el tratamiento de fallos entre servicios deben acordarse antes de implementar esas llamadas.

## Reglas de implementación compartidas

- Usar `INT` en MySQL e `Integer` en Java para todos los ID y cantidades. No usar `BIGINT` ni `Long`.
- Configurar `server.port=8083`.
- Mantener las cinco tablas exclusivamente en `db_inventario`.
- No crear tablas de recetas ni dispensaciones dentro de Inventario.
- No agregar precios ni campos `FLOAT`: el caso oficial no contempla cálculos monetarios.
- Mantener las credenciales y demás valores secretos fuera del repositorio.

## Alcance de lo acordado

**Exigido por el caso y los requisitos entregados:** gestionar el catálogo y el stock disponible por sucursal; verificar y reservar stock después de la emisión de la receta; actualizar Inventario cuando se realiza una dispensación. La reserva debe procesarse mediante SQS y Lambda, sin bloquear al médico.

**Definido por la propuesta del equipo para implementarlo:** tablas `MEDICAMENTO`, `SUCURSAL`, `STOCK`, `RESERVA_STOCK` y `DETALLE_RESERVA`; sus relaciones y restricciones; las clases y tipos de datos indicados arriba; MySQL Server 8.4 y puerto local `8083`.

Inventario determina y registra las reservas. **Recetas es el único propietario del estado de la receta.**
