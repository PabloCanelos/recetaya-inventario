
package com.inventario.service;

import com.inventario.entity.SucursalEntity;
import com.inventario.repository.SucursalRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SucursalService {

    @Autowired
    private SucursalRepository sucursalRepository;

    public List<SucursalEntity> listarSucursales() {
        return sucursalRepository.findAll();
    }

    public SucursalEntity buscarSucursalPorId(Integer idSucursal) {
        return sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new RuntimeException(
                        "Sucursal no encontrada con ID: " + idSucursal
                ));
    }

    public SucursalEntity guardarSucursal(SucursalEntity sucursal) {
        return sucursalRepository.save(sucursal);
    }

    public SucursalEntity actualizarSucursal(
            Integer idSucursal,
            SucursalEntity datosActualizados) {

        SucursalEntity sucursal = buscarSucursalPorId(idSucursal);

        sucursal.setNombre(datosActualizados.getNombre());
        sucursal.setDireccion(datosActualizados.getDireccion());

        return sucursalRepository.save(sucursal);
    }

    public void eliminarSucursal(Integer idSucursal) {
        SucursalEntity sucursal = buscarSucursalPorId(idSucursal);
        sucursalRepository.delete(sucursal);
    }
}
