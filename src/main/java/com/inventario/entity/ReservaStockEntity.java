
package com.inventario.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "reserva_stock",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_reserva_stock_id_receta",
            columnNames = {"id_receta"}
        )
    }
)
public class ReservaStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @Column(name = "id_receta", nullable = false)
    private Integer idReceta;

    @ManyToOne
    @JoinColumn(name = "id_sucursal", nullable = false)
    private SucursalEntity sucursal;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @OneToMany(
        mappedBy = "reserva",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<DetalleReservaEntity> detalles = new ArrayList<>();

    // CONSTRUCTOR VACIO

    public ReservaStockEntity() {
    }

    // GETTERS Y SETTERS

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

    public SucursalEntity getSucursal() {
        return sucursal;
    }

    public void setSucursal(SucursalEntity sucursal) {
        this.sucursal = sucursal;
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

    public List<DetalleReservaEntity> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleReservaEntity> detalles) {
        this.detalles = detalles;
    }
}
