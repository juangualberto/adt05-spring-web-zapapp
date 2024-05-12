package com.iesvdc.acceso.zapateria.zapapp.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iesvdc.acceso.zapateria.zapapp.modelos.Categoria;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Producto;

@Repository
public interface RepoProducto extends JpaRepository<Producto, Long> {
    //@Query("SELECT p FROM Producto p WHERE p.categoria = :padre")
    List<Producto> findByCategoria(Categoria padre);

}
