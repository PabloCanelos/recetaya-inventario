
package com.inventario.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaStockResponseDTO {

    private Integer idReserva;
    private Integer idReceta;
    private Integer idSucursal;
    private String nombreSucursal;
    private LocalDateTime fechaReserva;
    private String estado;

    private List<DetalleReservaResponseDTO> detalles = new ArrayList<>();

    public ReservaStockResponseDTO() {
    }

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public Integer getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(Integer idReceta) {
        this.idReceta = idReceta;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<DetalleReservaResponseDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleReservaResponseDTO> detalles) {
        this.detalles = detalles;
    }
}
