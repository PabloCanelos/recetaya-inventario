
package com.inventario.repository;

import com.inventario.entity.ReservaStockEntity;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservaStockRepository
        extends JpaRepository<ReservaStockEntity, Integer> {

    // Buscar reservas por ID de receta
    List<ReservaStockEntity> findByIdReceta(Integer idReceta);

    // Bloquear una reserva durante su actualización
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT r
            FROM ReservaStockEntity r
            WHERE r.idReserva = :idReserva
            """)
    Optional<ReservaStockEntity> buscarReservaParaActualizar(
            @Param("idReserva") Integer idReserva
    );
}
