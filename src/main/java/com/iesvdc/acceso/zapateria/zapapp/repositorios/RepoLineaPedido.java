package com.iesvdc.acceso.zapateria.zapapp.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iesvdc.acceso.zapateria.zapapp.modelos.LineaPedido;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Pedido;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Usuario;

import java.util.List;


@Repository
public interface RepoLineaPedido extends JpaRepository<LineaPedido, Long> {
    
    List<LineaPedido> findByPedido(Pedido pedido);


    @Query("SELECT lp " +
       "FROM LineaPedido lp " +
       "JOIN Pedido pedido on lp.pedido = pedido " +
       "WHERE lp = :lineaPedido " +
       "AND pedido.cliente = :usuario")
    List<LineaPedido> lineaPedidoBelongsToUser(LineaPedido lineaPedido, Usuario usuario);
    
}
