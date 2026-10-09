
package com.inventario.controller;

import com.inventario.dto.ReservaStockRequestDTO;
import com.inventario.dto.ReservaStockResponseDTO;
import com.inventario.service.ReservaStockService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaStockController {

    @Autowired
    private ReservaStockService reservaStockService;

    @GetMapping
    public List<ReservaStockResponseDTO> listarReservas() {
        return reservaStockService.listarReservas();
    }

    @GetMapping("/{idReserva}")
    public ReservaStockResponseDTO buscarReservaPorId(
            @PathVariable Integer idReserva) {

        return reservaStockService.buscarReservaPorId(idReserva);
    }

    @GetMapping("/receta/{idReceta}")
    public ReservaStockResponseDTO buscarReservaPorReceta(
            @PathVariable Integer idReceta) {

        return reservaStockService.buscarReservaPorReceta(idReceta);
    }

    @PostMapping
    public ReservaStockResponseDTO reservarMedicamento(
            @RequestBody ReservaStockRequestDTO request) {

        return reservaStockService.reservarMedicamento(
                request.getIdReceta(),
                request.getIdSucursal(),
                request.getIdMedicamento(),
                request.getCantidad()
        );
    }

    @PutMapping("/{idReserva}/cancelar")
    public ReservaStockResponseDTO cancelarReserva(
            @PathVariable Integer idReserva) {

        return reservaStockService.cancelarReserva(idReserva);
    }
}
