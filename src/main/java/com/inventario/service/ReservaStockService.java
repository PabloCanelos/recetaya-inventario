
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

    @Transactional(readOnly = true)
    public List<ReservaStockResponseDTO> listarReservas() {

        return reservaStockRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaStockResponseDTO buscarReservaPorId(Integer idReserva) {

        ReservaStockEntity reserva = reservaStockRepository
                .findById(idReserva)
                .orElseThrow(() -> new RuntimeException(
                        "Reserva no encontrada con ID: " + idReserva
                ));

        return convertirADTO(reserva);
    }

    @Transactional(readOnly = true)
    public ReservaStockResponseDTO buscarReservaPorReceta(Integer idReceta) {

        List<ReservaStockEntity> reservas =
                reservaStockRepository.findByIdReceta(idReceta);

        if (reservas.isEmpty()) {
            throw new RuntimeException(
                    "No existe reserva para la receta: " + idReceta
            );
        }

        return convertirADTO(reservas.get(0));
    }

    @Transactional
    public ReservaStockResponseDTO reservarMedicamento(
            Integer idReceta,
            Integer idSucursal,
            Integer idMedicamento,
            Integer cantidad) {

        if (idReceta == null || idSucursal == null ||
                idMedicamento == null || cantidad == null) {

            throw new IllegalArgumentException(
                    "Todos los campos de la reserva son obligatorios"
            );
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }

        if (!reservaStockRepository.findByIdReceta(idReceta).isEmpty()) {
            throw new IllegalArgumentException(
                    "Ya existe una reserva para esta receta"
            );
        }

        SucursalEntity sucursal = sucursalRepository
                .findById(idSucursal)
                .orElseThrow(() -> new RuntimeException(
                        "Sucursal no encontrada"
                ));

        MedicamentoEntity medicamento = medicamentoRepository
                .findById(idMedicamento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medicamento no encontrado"));

        StockEntity stock = stockRepository
                .findByMedicamento_IdMedicamentoAndSucursal_IdSucursal(
                        idMedicamento,
                        idSucursal
                )
                .orElseThrow(() -> new RuntimeException(
                        "No existe stock para este medicamento en esta sucursal"
                ));

        if (stock.getCantidadDisponible() < cantidad) {
            throw new IllegalArgumentException(
                    "Stock insuficiente para realizar la reserva"
            );
        }

        stock.setCantidadDisponible(
                stock.getCantidadDisponible() - cantidad
        );

        stock.setCantidadReservada(
                stock.getCantidadReservada() + cantidad
        );

        stockRepository.save(stock);

        ReservaStockEntity reserva = new ReservaStockEntity();

        reserva.setIdReceta(idReceta);
        reserva.setSucursal(sucursal);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setEstado("ACTIVA");

        DetalleReservaEntity detalle = new DetalleReservaEntity();

        detalle.setReserva(reserva);
        detalle.setMedicamento(medicamento);
        detalle.setCantidad(cantidad);

        List<DetalleReservaEntity> detalles = new ArrayList<>();
        detalles.add(detalle);

        reserva.setDetalles(detalles);

        ReservaStockEntity guardada = reservaStockRepository.save(reserva);

        return convertirADTO(guardada);
    }

    @Transactional
    public ReservaStockResponseDTO cancelarReserva(Integer idReserva) {

        ReservaStockEntity reserva = reservaStockRepository
                .findById(idReserva)
                .orElseThrow(() -> new RuntimeException(
                        "Reserva no encontrada"
                ));

        if (!"ACTIVA".equals(reserva.getEstado())) {
            throw new IllegalArgumentException(
                    "Solo se pueden cancelar reservas activas"
            );
        }

        for (DetalleReservaEntity detalle : reserva.getDetalles()) {

            Integer idMedicamento = detalle.getMedicamento()
                    .getIdMedicamento();

            Integer idSucursal = reserva.getSucursal()
                    .getIdSucursal();

            StockEntity stock = stockRepository
                    .findByMedicamento_IdMedicamentoAndSucursal_IdSucursal(
                            idMedicamento,
                            idSucursal
                    )
                    .orElseThrow(() -> new RuntimeException(
                            "No se encontró el stock asociado a la reserva"
                    ));

            stock.setCantidadDisponible(
                    stock.getCantidadDisponible() + detalle.getCantidad()
            );

            stock.setCantidadReservada(
                    stock.getCantidadReservada() - detalle.getCantidad()
            );

            stockRepository.save(stock);
        }

        reserva.setEstado("CANCELADA");

        ReservaStockEntity actualizada = reservaStockRepository.save(reserva);

        return convertirADTO(actualizada);
    }

    public ReservaStockResponseDTO convertirADTO(ReservaStockEntity reserva) {

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

        List<DetalleReservaResponseDTO> detallesDTO = new ArrayList<>();

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
