
package com.inventario.dto;

public class DetalleReservaDTO {

    private Integer idMedicamento;
    private Integer cantidad;

    public DetalleReservaDTO() {
    }

    public Integer getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(Integer idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
