
package com.inventario.service;

import com.inventario.entity.SucursalEntity;
import com.inventario.exception.RecursoNoEncontradoException;
import com.inventario.repository.SucursalRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SucursalService {

    @Autowired
    private SucursalRepository sucursalRepository;

    @Transactional(readOnly = true)
    public List<SucursalEntity> listarSucursales() {
        return sucursalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public SucursalEntity buscarSucursalPorId(Integer idSucursal) {
        return sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Sucursal no encontrada con ID: " + idSucursal
                ));
    }

    @Transactional
    public SucursalEntity guardarSucursal(SucursalEntity sucursal) {
        return sucursalRepository.save(sucursal);
    }

    @Transactional
    public SucursalEntity actualizarSucursal(
            Integer idSucursal,
            SucursalEntity datosActualizados) {

        SucursalEntity sucursal = buscarSucursalPorId(idSucursal);

        sucursal.setNombre(datosActualizados.getNombre());
        sucursal.setDireccion(datosActualizados.getDireccion());

        return sucursalRepository.save(sucursal);
    }

    @Transactional
    public void eliminarSucursal(Integer idSucursal) {
        SucursalEntity sucursal = buscarSucursalPorId(idSucursal);
        sucursalRepository.delete(sucursal);
    }
}
