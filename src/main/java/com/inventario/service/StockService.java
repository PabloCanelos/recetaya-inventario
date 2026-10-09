
package com.inventario.service;

import com.inventario.dto.StockResponseDTO;
import com.inventario.entity.MedicamentoEntity;
import com.inventario.entity.StockEntity;
import com.inventario.entity.SucursalEntity;
import com.inventario.repository.MedicamentoRepository;
import com.inventario.repository.StockRepository;
import com.inventario.repository.SucursalRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Transactional(readOnly = true)
    public List<StockResponseDTO> listarStock() {
        return stockRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public StockResponseDTO buscarStockPorId(Integer idStock) {
        StockEntity stock = stockRepository.findById(idStock)
                .orElseThrow(() -> new RuntimeException(
                        "Stock no encontrado con ID: " + idStock
                ));

        return convertirADTO(stock);
    }

    @Transactional(readOnly = true)
    public StockResponseDTO buscarStockPorMedicamentoYSucursal(
            Integer idMedicamento,
            Integer idSucursal) {

        StockEntity stock = stockRepository
                .findByMedicamento_IdMedicamentoAndSucursal_IdSucursal(
                        idMedicamento,
                        idSucursal
                )
                .orElseThrow(() -> new RuntimeException(
                        "No existe stock para el medicamento y sucursal indicados"
                ));

        return convertirADTO(stock);
    }

    @Transactional
    public StockResponseDTO registrarStock(
            Integer idMedicamento,
            Integer idSucursal,
            Integer cantidad) {

        if (idMedicamento == null || idSucursal == null || cantidad == null) {
            throw new IllegalArgumentException(
                    "Medicamento, sucursal y cantidad son obligatorios"
            );
        }

        if (cantidad < 0) {
            throw new IllegalArgumentException(
                    "La cantidad no puede ser negativa"
            );
        }

        MedicamentoEntity medicamento = medicamentoRepository
                .findById(idMedicamento)
                .orElseThrow(() -> new RuntimeException(
                        "Medicamento no encontrado"
                ));

        SucursalEntity sucursal = sucursalRepository
                .findById(idSucursal)
                .orElseThrow(() -> new RuntimeException(
                        "Sucursal no encontrada"
                ));

        if (stockRepository
                .findByMedicamento_IdMedicamentoAndSucursal_IdSucursal(
                        idMedicamento,
                        idSucursal
                ).isPresent()) {

            throw new IllegalArgumentException(
                    "Ya existe stock para este medicamento en esta sucursal"
            );
        }

        StockEntity stock = new StockEntity();
        stock.setMedicamento(medicamento);
        stock.setSucursal(sucursal);
        stock.setCantidadDisponible(cantidad);
        stock.setCantidadReservada(0);

        StockEntity guardado = stockRepository.save(stock);

        return convertirADTO(guardado);
    }

    public StockResponseDTO convertirADTO(StockEntity stock) {

        StockResponseDTO dto = new StockResponseDTO();

        dto.setIdStock(stock.getIdStock());

        dto.setIdMedicamento(
                stock.getMedicamento().getIdMedicamento()
        );

        dto.setNombreMedicamento(
                stock.getMedicamento().getNombre()
        );

        dto.setIdSucursal(
                stock.getSucursal().getIdSucursal()
        );

        dto.setNombreSucursal(
                stock.getSucursal().getNombre()
        );

        dto.setCantidadDisponible(
                stock.getCantidadDisponible()
        );

        dto.setCantidadReservada(
                stock.getCantidadReservada()
        );

        return dto;
    }
}
