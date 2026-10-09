
package com.inventario.repository;

import com.inventario.entity.DetalleReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleReservaRepository
        extends JpaRepository<DetalleReservaEntity, Integer> {

    List<DetalleReservaEntity> findByReserva_IdReserva(Integer idReserva);

}
