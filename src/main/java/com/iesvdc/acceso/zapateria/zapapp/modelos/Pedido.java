package com.iesvdc.acceso.zapateria.zapapp.modelos;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha;
    private String observaciones;
    private Float descuento;
    @Enumerated(EnumType.STRING)
    private Estado estado;
    @OneToMany(mappedBy = "pedido")
    private List<LineaPedido> lineaPedidos;
    @ManyToOne
    private Usuario asignadoOperario;
    @ManyToOne
    private Direccion direccion;
}
