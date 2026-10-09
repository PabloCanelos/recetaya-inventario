
package com.inventario.repository;

import com.inventario.entity.StockEntity;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StockRepository extends JpaRepository<StockEntity, Integer> {

    // Consulta normal de stock
    Optional<StockEntity> findByMedicamento_IdMedicamentoAndSucursal_IdSucursal(
            Integer idMedicamento,
            Integer idSucursal
    );

    // Bloqueo para reservas y cancelaciones
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM StockEntity s
            WHERE s.medicamento.idMedicamento = :idMedicamento
              AND s.sucursal.idSucursal = :idSucursal
            """)
    Optional<StockEntity> buscarStockParaActualizar(
            @Param("idMedicamento") Integer idMedicamento,
            @Param("idSucursal") Integer idSucursal
    );

    // Bloqueo para actualizar o eliminar stock por ID
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM StockEntity s
            WHERE s.idStock = :idStock
            """)
    Optional<StockEntity> buscarStockPorIdParaActualizar(
            @Param("idStock") Integer idStock
    );
}
