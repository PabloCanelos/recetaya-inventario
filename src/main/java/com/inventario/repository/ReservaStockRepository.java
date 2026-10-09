
package com.inventario.repository;

import com.inventario.entity.ReservaStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaStockRepository
        extends JpaRepository<ReservaStockEntity, Integer> {

    List<ReservaStockEntity> findByIdReceta(Integer idReceta);

}
