
package com.inventario.service;

import com.inventario.dto.DetalleReservaResponseDTO;
import com.inventario.dto.ReservaStockResponseDTO;

import com.inventario.exception.RecursoNoEncontradoException;

import com.inventario.entity.DetalleReservaEntity;
import com.inventario.entity.MedicamentoEntity;
import com.inventario.entity.ReservaStockEntity;
import com.inventario.entity.StockEntity;
import com.inventario.entity.SucursalEntity;

import com.inventario.repository.MedicamentoRepository;
import com.inventario.repository.ReservaStockRepository;
import com.inventario.repository.StockRepository;
import com.inventario.repository.SucursalRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservaStockService {

    @Autowired
    private ReservaStockRepository reservaStockRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    // LISTAR TODAS LAS RESERVAS

    @Transactional(readOnly = true)
    public List<ReservaStockResponseDTO> listarReservas() {

        return reservaStockRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    // BUSCAR RESERVA POR ID

    @Transactional(readOnly = true)
    public ReservaStockResponseDTO buscarReservaPorId(Integer idReserva) {

        ReservaStockEntity reserva = reservaStockRepository
                .findById(idReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reserva no encontrada con ID: " + idReserva
                ));

        return convertirADTO(reserva);
    }

    // BUSCAR RESERVA POR RECETA

    @Transactional(readOnly = true)
    public ReservaStockResponseDTO buscarReservaPorReceta(Integer idReceta) {

        List<ReservaStockEntity> reservas =
                reservaStockRepository.findByIdReceta(idReceta);

        if (reservas.isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "No existe reserva para la receta: " + idReceta
            );
        }

        return convertirADTO(reservas.get(0));
    }

    // RESERVAR MEDICAMENTO

    @Transactional
    public ReservaStockResponseDTO reservarMedicamento(
            Integer idReceta,
            Integer idSucursal,
            Integer idMedicamento,
            Integer cantidad) {

        // Validar campos obligatorios

        if (idReceta == null || idSucursal == null ||
                idMedicamento == null || cantidad == null) {

            throw new IllegalArgumentException(
                    "Todos los campos de la reserva son obligatorios"
            );
        }

        // Validar cantidad positiva

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }

        // Evitar reservas duplicadas para la misma receta

        if (!reservaStockRepository.findByIdReceta(idReceta).isEmpty()) {
            throw new IllegalStateException(
                    "Ya existe una reserva para esta receta"
            );
        }

        // Verificar existencia de sucursal

        SucursalEntity sucursal = sucursalRepository
                .findById(idSucursal)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Sucursal no encontrada con ID: " + idSucursal
                ));

        // Verificar existencia de medicamento

        MedicamentoEntity medicamento = medicamentoRepository
                .findById(idMedicamento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Medicamento no encontrado con ID: " + idMedicamento
                ));

        // Obtener stock con bloqueo pesimista de escritura

        StockEntity stock = stockRepository
                .buscarStockParaActualizar(
                        idMedicamento,
                        idSucursal
                )
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe stock para este medicamento en esta sucursal"
                ));

        // Validar stock suficiente

        if (stock.getCantidadDisponible() < cantidad) {
            throw new IllegalArgumentException(
                    "Stock insuficiente para realizar la reserva"
            );
        }

        // Descontar stock disponible

        stock.setCantidadDisponible(
                stock.getCantidadDisponible() - cantidad
        );

        // Aumentar stock reservado

        stock.setCantidadReservada(
                stock.getCantidadReservada() + cantidad
        );

        stockRepository.save(stock);

        // Crear reserva

        ReservaStockEntity reserva = new ReservaStockEntity();

        reserva.setIdReceta(idReceta);
        reserva.setSucursal(sucursal);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setEstado("ACTIVA");

        // Crear detalle de reserva

        DetalleReservaEntity detalle = new DetalleReservaEntity();

        detalle.setReserva(reserva);
        detalle.setMedicamento(medicamento);
        detalle.setCantidad(cantidad);

        List<DetalleReservaEntity> detalles = new ArrayList<>();
        detalles.add(detalle);

        reserva.setDetalles(detalles);

        // Guardar reserva y sus detalles

        ReservaStockEntity guardada =
                reservaStockRepository.save(reserva);

        return convertirADTO(guardada);
    }

    // CANCELAR RESERVA

    @Transactional
    public ReservaStockResponseDTO cancelarReserva(Integer idReserva) {

        // Buscar y bloquear la reserva para su actualización

        ReservaStockEntity reserva = reservaStockRepository
                .buscarReservaParaActualizar(idReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reserva no encontrada con ID: " + idReserva
                ));

        // Verificar que la reserva esté activa

        if (!"ACTIVA".equals(reserva.getEstado())) {
            throw new IllegalStateException(
                    "Solo se pueden cancelar reservas activas"
            );
        }

        // Recuperar stock de cada detalle

        for (DetalleReservaEntity detalle : reserva.getDetalles()) {

            Integer idMedicamento = detalle.getMedicamento()
                    .getIdMedicamento();

            Integer idSucursal = reserva.getSucursal()
                    .getIdSucursal();

            // Obtener stock con bloqueo pesimista de escritura

            StockEntity stock = stockRepository
                    .buscarStockParaActualizar(
                            idMedicamento,
                            idSucursal
                    )
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró el stock asociado a la reserva"
                    ));

            // Impedir que el stock reservado quede negativo

            if (stock.getCantidadReservada() < detalle.getCantidad()) {
                throw new IllegalStateException(
                        "El stock reservado es insuficiente para cancelar la reserva"
                );
            }

            // Recuperar stock disponible

            stock.setCantidadDisponible(
                    stock.getCantidadDisponible() + detalle.getCantidad()
            );

            // Descontar stock reservado

            stock.setCantidadReservada(
                    stock.getCantidadReservada() - detalle.getCantidad()
            );

            stockRepository.save(stock);
        }

        // Actualizar estado de la reserva

        reserva.setEstado("CANCELADA");

        ReservaStockEntity actualizada =
                reservaStockRepository.save(reserva);

        return convertirADTO(actualizada);
    }

    // CONFIRMAR DISPENSACIÓN

    @Transactional
    public ReservaStockResponseDTO confirmarDispensacion(Integer idReserva) {

        // Bloquear la reserva para impedir operaciones simultáneas

        ReservaStockEntity reserva = reservaStockRepository
                .buscarReservaParaActualizar(idReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reserva no encontrada con ID: " + idReserva
                ));

        // Solo se pueden dispensar reservas activas

        if (!"ACTIVA".equals(reserva.getEstado())) {
            throw new IllegalStateException(
                    "Solo se pueden dispensar reservas activas"
            );
        }

        // Procesar cada medicamento reservado

        for (DetalleReservaEntity detalle : reserva.getDetalles()) {

            Integer idMedicamento = detalle.getMedicamento()
                    .getIdMedicamento();

            Integer idSucursal = reserva.getSucursal()
                    .getIdSucursal();

            // Bloquear el stock correspondiente

            StockEntity stock = stockRepository
                    .buscarStockParaActualizar(
                            idMedicamento,
                            idSucursal
                    )
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró el stock asociado a la reserva"
                    ));

            // Validar unidades reservadas suficientes

            if (stock.getCantidadReservada() < detalle.getCantidad()) {
                throw new IllegalStateException(
                        "Stock reservado insuficiente para dispensar"
                );
            }

            // El stock disponible ya fue descontado al reservar.
            // Solo se descuentan las unidades reservadas.

            stock.setCantidadReservada(
                    stock.getCantidadReservada() - detalle.getCantidad()
            );

            stockRepository.save(stock);
        }

        // Marcar la reserva como dispensada

        reserva.setEstado("DISPENSADA");

        ReservaStockEntity actualizada =
                reservaStockRepository.save(reserva);

        return convertirADTO(actualizada);
    }

    // CONVERTIR ENTIDAD A DTO

    public ReservaStockResponseDTO convertirADTO(
            ReservaStockEntity reserva) {

        ReservaStockResponseDTO dto = new ReservaStockResponseDTO();

        dto.setIdReserva(reserva.getIdReserva());
        dto.setIdReceta(reserva.getIdReceta());

        dto.setIdSucursal(
                reserva.getSucursal().getIdSucursal()
        );

        dto.setNombreSucursal(
                reserva.getSucursal().getNombre()
        );

        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setEstado(reserva.getEstado());

        List<DetalleReservaResponseDTO> detallesDTO =
                new ArrayList<>();

        for (DetalleReservaEntity detalle : reserva.getDetalles()) {

            DetalleReservaResponseDTO detalleDTO =
                    new DetalleReservaResponseDTO();

            detalleDTO.setIdDetalleReserva(
                    detalle.getIdDetalleReserva()
            );

            detalleDTO.setIdMedicamento(
                    detalle.getMedicamento().getIdMedicamento()
            );

            detalleDTO.setNombreMedicamento(
                    detalle.getMedicamento().getNombre()
            );

            detalleDTO.setCantidad(detalle.getCantidad());

            detallesDTO.add(detalleDTO);
        }

        dto.setDetalles(detallesDTO);

        return dto;
    }
}
