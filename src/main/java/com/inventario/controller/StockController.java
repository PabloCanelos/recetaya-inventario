
package com.inventario.controller;

import com.inventario.dto.StockRequestDTO;
import com.inventario.dto.StockResponseDTO;
import com.inventario.service.StockService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping
    public List<StockResponseDTO> listarStock() {
        return stockService.listarStock();
    }

    @GetMapping("/{idStock}")
    public StockResponseDTO buscarStockPorId(
            @PathVariable Integer idStock) {

        return stockService.buscarStockPorId(idStock);
    }

    @GetMapping("/consultar")
    public StockResponseDTO consultarStock(
            @RequestParam Integer idMedicamento,
            @RequestParam Integer idSucursal) {

        return stockService.buscarStockPorMedicamentoYSucursal(
                idMedicamento,
                idSucursal
        );
    }

    @PostMapping
    public StockResponseDTO registrarStock(
            @RequestBody StockRequestDTO request) {

        return stockService.registrarStock(
                request.getIdMedicamento(),
                request.getIdSucursal(),
                request.getCantidad()
        );
    }
}
