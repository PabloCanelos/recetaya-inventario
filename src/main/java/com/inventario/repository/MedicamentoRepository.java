
package com.inventario.repository;

import com.inventario.entity.MedicamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentoRepository
        extends JpaRepository<MedicamentoEntity, Integer> {

}
