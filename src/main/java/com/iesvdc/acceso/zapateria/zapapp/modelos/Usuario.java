package com.iesvdc.acceso.zapateria.zapapp.modelos;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String username;
    private String password;
    
    @OneToMany(mappedBy = "usuario")
    private List<Direccion> direcciones;
    @OneToMany(mappedBy = "usuario")
    private List<Telefono> telefonos;
    @OneToMany(mappedBy = "usuario")
    private List<RolUsuario> roles;

}
