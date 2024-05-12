package com.iesvdc.acceso.zapateria.zapapp.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iesvdc.acceso.zapateria.zapapp.modelos.Telefono;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Usuario;

import java.util.List;


@Repository
public interface RepoTelefono extends JpaRepository<Telefono, Long> {

    List<Telefono> findByUsuario(Usuario usuario);
}
