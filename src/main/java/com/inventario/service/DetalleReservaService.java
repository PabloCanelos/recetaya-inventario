
package com.inventario.service;

import com.inventario.entity.DetalleReservaEntity;
import com.inventario.repository.DetalleReservaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DetalleReservaService {

    @Autowired
    private DetalleReservaRepository detalleReservaRepository;

    @Transactional(readOnly = true)
    public List<DetalleReservaEntity> listarDetalles() {
        return detalleReservaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public DetalleReservaEntity buscarDetallePorId(Integer idDetalleReserva) {

        return detalleReservaRepository.findById(idDetalleReserva)
                .orElseThrow(() -> new RuntimeException(
                        "Detalle de reserva no encontrado con ID: "
                                + idDetalleReserva
                ));
    }

    @Transactional(readOnly = true)
    public List<DetalleReservaEntity> buscarDetallesPorReserva(
            Integer idReserva) {

        return detalleReservaRepository
                .findByReserva_IdReserva(idReserva);
    }
}
