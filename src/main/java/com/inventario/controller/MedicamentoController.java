
package com.inventario.controller;

import com.inventario.entity.MedicamentoEntity;
import com.inventario.service.MedicamentoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    @GetMapping
    public List<MedicamentoEntity> listarMedicamentos() {
        return medicamentoService.listarMedicamentos();
    }

    @GetMapping("/{idMedicamento}")
    public MedicamentoEntity buscarMedicamentoPorId(
            @PathVariable Integer idMedicamento) {

        return medicamentoService.buscarMedicamentoPorId(idMedicamento);
    }

    @PostMapping
    public MedicamentoEntity guardarMedicamento(
            @RequestBody MedicamentoEntity medicamento) {

        return medicamentoService.guardarMedicamento(medicamento);
    }

    @PutMapping("/{idMedicamento}")
    public MedicamentoEntity actualizarMedicamento(
            @PathVariable Integer idMedicamento,
            @RequestBody MedicamentoEntity medicamento) {

        return medicamentoService.actualizarMedicamento(
                idMedicamento,
                medicamento
        );
    }

    @DeleteMapping("/{idMedicamento}")
    public void eliminarMedicamento(
            @PathVariable Integer idMedicamento) {

        medicamentoService.eliminarMedicamento(idMedicamento);
    }
}
