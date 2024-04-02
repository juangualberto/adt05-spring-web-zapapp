package com.iesvdc.acceso.zapateria.zapapp.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iesvdc.acceso.zapateria.zapapp.modelos.RolUsuario;

@Repository
public interface RepoRolUsuario extends JpaRepository<RolUsuario, Long> {

}
