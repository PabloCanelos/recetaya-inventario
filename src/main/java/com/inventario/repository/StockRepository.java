
package com.inventario.repository;

import com.inventario.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StockRepository extends JpaRepository<StockEntity, Integer> {

    Optional<StockEntity> findByMedicamento_IdMedicamentoAndSucursal_IdSucursal(
            Integer idMedicamento,
            Integer idSucursal
    );

}
