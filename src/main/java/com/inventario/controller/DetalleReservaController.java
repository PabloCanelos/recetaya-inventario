
package com.inventario.controller;

import com.inventario.dto.DetalleReservaResponseDTO;
import com.inventario.entity.DetalleReservaEntity;
import com.inventario.service.DetalleReservaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/detalles-reserva")
public class DetalleReservaController {

    @Autowired
    private DetalleReservaService detalleReservaService;

    @GetMapping
    public List<DetalleReservaResponseDTO> listarDetalles() {

        List<DetalleReservaResponseDTO> resultado = new ArrayList<>();

        for (DetalleReservaEntity detalle :
                detalleReservaService.listarDetalles()) {

            resultado.add(convertirADTO(detalle));
        }

        return resultado;
    }

    @GetMapping("/{idDetalleReserva}")
    public DetalleReservaResponseDTO buscarDetallePorId(
            @PathVariable Integer idDetalleReserva) {

        DetalleReservaEntity detalle =
                detalleReservaService.buscarDetallePorId(idDetalleReserva);

        return convertirADTO(detalle);
    }

    @GetMapping("/reserva/{idReserva}")
    public List<DetalleReservaResponseDTO> buscarDetallesPorReserva(
            @PathVariable Integer idReserva) {

        List<DetalleReservaResponseDTO> resultado = new ArrayList<>();

        for (DetalleReservaEntity detalle :
                detalleReservaService.buscarDetallesPorReserva(idReserva)) {

            resultado.add(convertirADTO(detalle));
        }

        return resultado;
    }

    private DetalleReservaResponseDTO convertirADTO(
            DetalleReservaEntity detalle) {

        DetalleReservaResponseDTO dto = new DetalleReservaResponseDTO();

        dto.setIdDetalleReserva(detalle.getIdDetalleReserva());
        dto.setIdMedicamento(detalle.getMedicamento().getIdMedicamento());
        dto.setNombreMedicamento(detalle.getMedicamento().getNombre());
        dto.setCantidad(detalle.getCantidad());

        return dto;
    }
}
