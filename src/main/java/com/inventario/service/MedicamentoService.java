
package com.inventario.service;

import com.inventario.entity.MedicamentoEntity;
import com.inventario.exception.RecursoNoEncontradoException;
import com.inventario.repository.MedicamentoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Transactional(readOnly = true)
    public List<MedicamentoEntity> listarMedicamentos() {
        return medicamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public MedicamentoEntity buscarMedicamentoPorId(Integer idMedicamento) {
        return medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Medicamento no encontrado con ID: " + idMedicamento
                ));
    }

    @Transactional
    public MedicamentoEntity guardarMedicamento(MedicamentoEntity medicamento) {
        return medicamentoRepository.save(medicamento);
    }

    @Transactional
    public MedicamentoEntity actualizarMedicamento(
            Integer idMedicamento,
            MedicamentoEntity datosActualizados) {

        MedicamentoEntity medicamento = buscarMedicamentoPorId(idMedicamento);

        medicamento.setNombre(datosActualizados.getNombre());
        medicamento.setDescripcion(datosActualizados.getDescripcion());
        medicamento.setActivo(datosActualizados.getActivo());

        return medicamentoRepository.save(medicamento);
    }

    @Transactional
    public void eliminarMedicamento(Integer idMedicamento) {
        MedicamentoEntity medicamento = buscarMedicamentoPorId(idMedicamento);
        medicamentoRepository.delete(medicamento);
    }
}
