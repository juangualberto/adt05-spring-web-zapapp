package com.iesvdc.acceso.zapateria.zapapp.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.iesvdc.acceso.zapateria.zapapp.modelos.Rol;
import com.iesvdc.acceso.zapateria.zapapp.modelos.RolUsuario;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Telefono;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Usuario;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoRolUsuario;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoTelefono;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoUsuario;




@Controller
public class ControGeneral {

    @Autowired
    private RepoTelefono repoTelefono;

    @Autowired
    private RepoUsuario repoUsuario;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RepoRolUsuario repoRolUsuario;

    
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/ayuda")
    public String ayuda() {
        return "ayuda";
    }

    @GetMapping("/error")
    public String showError(Model model) {

        model.addAttribute("titulo", "ERROR");
        model.addAttribute("mensaje", "Error genérico");

        return "error";
    }

    @GetMapping("/acerca")
    public String showAcerca() {
        return "acerca";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }
    
    @PostMapping("/register")
    public String register(
        Model modelo, 
        @NonNull @RequestParam String username,
        @NonNull @RequestParam String password,
        @NonNull @RequestParam String nombre,
        @NonNull @RequestParam String apellidos,
        @NonNull @RequestParam Long pais,
        @NonNull @RequestParam Long telefono,
        @NonNull @RequestParam String email) {        

        Telefono tel = new Telefono();
        Usuario usuario = new Usuario();
        //BCryptPasswordEncoder bPasswordEncoder = new BCryptPasswordEncoder();
        
        try {
            usuario.setNombre(nombre);
            usuario.setApellido(apellidos);
            usuario.setUsername(username);
            usuario.setEmail(email);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setEnabled(true);
            usuario = repoUsuario.save(usuario);
            
            tel.setNumero(telefono);
            tel.setCodigoPais(pais);
            tel.setUsuario(usuario);
            repoTelefono.save(tel);

            RolUsuario rolUsuario = new RolUsuario();
            rolUsuario.setRol(Rol.CLIENTE);
            rolUsuario.setUsuario(usuario);
            repoRolUsuario.save(rolUsuario);

            modelo.addAttribute("titulo", "Alta nuevo usuario");
            modelo.addAttribute("mensaje", "El usuario "+username+" está siendo creado.");
            
        } catch (Exception e){
            modelo.addAttribute("titulo", "Error al crear nuevo usuario");
            modelo.addAttribute("mensaje", "El usuario "+username+" no ha podido crearse."+
                "Probablemente el username o email introducidos ya existen en la base de datos.");
        }

        return "error";
    }
}
