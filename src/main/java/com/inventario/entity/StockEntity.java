
package com.inventario.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "stock")
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stock")
    private Integer idStock;

    @ManyToOne
    @JoinColumn(name = "id_medicamento", nullable = false)
    private MedicamentoEntity medicamento;

    @ManyToOne
    @JoinColumn(name = "id_sucursal", nullable = false)
    private SucursalEntity sucursal;

    @Column(name = "cantidad_disponible", nullable = false)
    private Integer cantidadDisponible;

    @Column(name = "cantidad_reservada", nullable = false)
    private Integer cantidadReservada;

    public StockEntity() {
    }

    public Integer getIdStock() {
        return idStock;
    }

    public void setIdStock(Integer idStock) {
        this.idStock = idStock;
    }

    public MedicamentoEntity getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(MedicamentoEntity medicamento) {
        this.medicamento = medicamento;
    }

    public SucursalEntity getSucursal() {
        return sucursal;
    }

    public void setSucursal(SucursalEntity sucursal) {
        this.sucursal = sucursal;
    }

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public Integer getCantidadReservada() {
        return cantidadReservada;
    }

    public void setCantidadReservada(Integer cantidadReservada) {
        this.cantidadReservada = cantidadReservada;
    }
}
