
package com.inventario.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_reserva")
public class DetalleReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_reserva")
    private Integer idDetalleReserva;

    @ManyToOne
    @JoinColumn(name = "id_reserva", nullable = false)
    private ReservaStockEntity reserva;

    @ManyToOne
    @JoinColumn(name = "id_medicamento", nullable = false)
    private MedicamentoEntity medicamento;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    public DetalleReservaEntity() {
    }

    public Integer getIdDetalleReserva() {
        return idDetalleReserva;
    }

    public void setIdDetalleReserva(Integer idDetalleReserva) {
        this.idDetalleReserva = idDetalleReserva;
    }

    public ReservaStockEntity getReserva() {
        return reserva;
    }

    public void setReserva(ReservaStockEntity reserva) {
        this.reserva = reserva;
    }

    public MedicamentoEntity getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(MedicamentoEntity medicamento) {
        this.medicamento = medicamento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
