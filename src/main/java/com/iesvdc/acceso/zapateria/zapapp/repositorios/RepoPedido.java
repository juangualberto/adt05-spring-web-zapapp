package com.iesvdc.acceso.zapateria.zapapp.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iesvdc.acceso.zapateria.zapapp.modelos.Pedido;

public interface RepoPedido extends JpaRepository <Pedido, Long>{
    
}
