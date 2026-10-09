
package com.inventario.dto;

public class DetalleReservaResponseDTO {

    private Integer idDetalleReserva;
    private Integer idMedicamento;
    private String nombreMedicamento;
    private Integer cantidad;

    public DetalleReservaResponseDTO() {
    }

    public Integer getIdDetalleReserva() {
        return idDetalleReserva;
    }

    public void setIdDetalleReserva(Integer idDetalleReserva) {
        this.idDetalleReserva = idDetalleReserva;
    }

    public Integer getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(Integer idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public String getNombreMedicamento() {
        return nombreMedicamento;
    }

    public void setNombreMedicamento(String nombreMedicamento) {
        this.nombreMedicamento = nombreMedicamento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
