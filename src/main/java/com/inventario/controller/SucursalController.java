
package com.inventario.controller;

import com.inventario.entity.SucursalEntity;
import com.inventario.service.SucursalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
public class SucursalController {

    @Autowired
    private SucursalService sucursalService;

    @GetMapping
    public List<SucursalEntity> listarSucursales() {
        return sucursalService.listarSucursales();
    }

    @GetMapping("/{idSucursal}")
    public SucursalEntity buscarSucursalPorId(
            @PathVariable Integer idSucursal) {

        return sucursalService.buscarSucursalPorId(idSucursal);
    }

    @PostMapping
    public SucursalEntity guardarSucursal(
            @RequestBody SucursalEntity sucursal) {

        return sucursalService.guardarSucursal(sucursal);
    }

    @PutMapping("/{idSucursal}")
    public SucursalEntity actualizarSucursal(
            @PathVariable Integer idSucursal,
            @RequestBody SucursalEntity sucursal) {

        return sucursalService.actualizarSucursal(
                idSucursal,
                sucursal
        );
    }

    @DeleteMapping("/{idSucursal}")
    public void eliminarSucursal(
            @PathVariable Integer idSucursal) {

        sucursalService.eliminarSucursal(idSucursal);
    }
}
