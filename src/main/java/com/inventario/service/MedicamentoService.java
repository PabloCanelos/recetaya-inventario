
package com.inventario.service;

import com.inventario.entity.MedicamentoEntity;
import com.inventario.repository.MedicamentoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    public List<MedicamentoEntity> listarMedicamentos() {
        return medicamentoRepository.findAll();
    }

    public MedicamentoEntity buscarMedicamentoPorId(Integer idMedicamento) {
        return medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new RuntimeException(
                        "Medicamento no encontrado con ID: " + idMedicamento
                ));
    }

    public MedicamentoEntity guardarMedicamento(MedicamentoEntity medicamento) {
        return medicamentoRepository.save(medicamento);
    }

    public MedicamentoEntity actualizarMedicamento(
            Integer idMedicamento,
            MedicamentoEntity datosActualizados) {

        MedicamentoEntity medicamento = buscarMedicamentoPorId(idMedicamento);

        medicamento.setNombre(datosActualizados.getNombre());
        medicamento.setDescripcion(datosActualizados.getDescripcion());
        medicamento.setActivo(datosActualizados.getActivo());

        return medicamentoRepository.save(medicamento);
    }

    public void eliminarMedicamento(Integer idMedicamento) {
        MedicamentoEntity medicamento = buscarMedicamentoPorId(idMedicamento);
        medicamentoRepository.delete(medicamento);
    }
}
