package com.iesvdc.acceso.zapateria.zapapp.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iesvdc.acceso.zapateria.zapapp.modelos.RolUsuario;
import java.util.List;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Usuario;


@Repository
public interface RepoRolUsuario extends JpaRepository<RolUsuario, Long> {
    List<RolUsuario> findByUsuario(Usuario usuario);
}
