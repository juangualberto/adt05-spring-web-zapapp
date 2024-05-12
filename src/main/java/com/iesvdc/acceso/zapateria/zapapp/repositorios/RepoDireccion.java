package com.iesvdc.acceso.zapateria.zapapp.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iesvdc.acceso.zapateria.zapapp.modelos.Direccion;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Usuario;

import java.util.List;


@Repository
public interface RepoDireccion extends JpaRepository<Direccion, Long> {

    List<Direccion> findByUsuario(Usuario usuario);
}
