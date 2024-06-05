package com.iesvdc.acceso.zapateria.zapapp.controladores;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.iesvdc.acceso.zapateria.zapapp.modelos.Direccion;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Rol;
import com.iesvdc.acceso.zapateria.zapapp.modelos.RolUsuario;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Telefono;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Usuario;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoDireccion;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoRolUsuario;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoTelefono;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoUsuario;

/**
 * Controlador para gestión de usuarios de la plataforma, para
 * cambiar el tipo de usuario (sólo lo puede hacer un admin)
 * o modificar datos de clientes y/o operarios que olvidaron su
 * contraseña.
 */
@Controller
@RequestMapping("/admin/usuarios")
public class ControUsuarios {

    @Autowired 
    private RepoUsuario repoUsuario;

    @Autowired 
    private RepoTelefono repoTelefono;

    @Autowired 
    private RepoDireccion repoDireccion;

    @Autowired
    private RepoRolUsuario repoRolUsuario;

    @GetMapping("")
    public String findAllUsers(Model modelo){
        modelo.addAttribute("usuarios", repoUsuario.findAll());
        return "admin/usuarios/usuarios";
    }    

    /**
     * Devuelve el formulario para añadir un nuevo usuario
     */
    @GetMapping("/add")
    public String addUsuario(Model modelo) {
        modelo.addAttribute("usuario", new Usuario());
        modelo.addAttribute("telefonos", repoTelefono.findAll());
        modelo.addAttribute("direcciones", repoDireccion.findAll());
        modelo.addAttribute("listaRoles", Rol.values());
        return "admin/usuarios/add";
    }

    /**
     * Recoge los datos del formulario anterior para crear un nuevo usuario
     */
    @PostMapping("/add")
    public String addUsuario(
            @ModelAttribute("usuario") @NonNull Usuario usuario,
            @RequestParam(name = "roles_usuario", required = false) List<String> listaRoles){        
        
        if (usuario.getPassword().length()<2) {
                usuario.setPassword(repoUsuario.findById(usuario.getId()).get().getPassword());
        } else {
                usuario.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));
        }

        repoUsuario.save(usuario);

        List<Direccion> direcciones = usuario.getDirecciones();
        if (direcciones != null) {
            for (Direccion direccion : direcciones) {
                if (direccion != null)
                    repoDireccion.save(direccion);
            }
        }
        
        List<Telefono> telefonos = usuario.getTelefonos();
        if (telefonos != null) {
            for (Telefono telefono : telefonos) {
                if (telefono != null)
                    repoTelefono.save(telefono);
            }
        }

        // borramos todos los permisos antiguos
        repoRolUsuario.deleteAll(repoRolUsuario.findByUsuario(usuario));
        // damos los nuevos permisos
        if (listaRoles!=null)
                for (String rol : listaRoles) {
                        RolUsuario ru = new RolUsuario();
                        ru.setUsuario(usuario);
                        ru.setRol(Rol.valueOf(rol));
                        repoRolUsuario.save(ru);                        
                }
         
        

        return "redirect:/admin/usuarios";
    }

    /**
     * Muestra un formulario para confirmar el borrado del usuario
     */
    @GetMapping("/delete/{id}")
    public String deleteUsuarioForm(Model modelo, @PathVariable("id") @NonNull Long id) {
        Optional<Usuario> oUsuario = repoUsuario.findById(id);
        if (oUsuario.isPresent())
            modelo.addAttribute(
                    "usuario", oUsuario.get());
        else {
            modelo.addAttribute(
                    "mensaje", "El usuario consultado no existe.");
            return "error";
        }
        return "admin/usuarios/delete";
    }

    /**
     * Elimina el usuario de la base de datos si es posible
     */
    @PostMapping("/delete/{id}")
    public String deleteUsuario(
            Model modelo,
            @PathVariable("id") @NonNull Long id) {
        try {
            repoUsuario.deleteById(id);
        } catch (Exception e) {
            modelo.addAttribute(
                    "mensaje", "El usuario no se puede eliminar porque tiene compras ya realizadas.");
            return "error";
        }

        return "redirect:/admin/usuarios";
    }

    /**
     * Muestra un formulario para editar el usuario, para guardar hacemos POST de
     * nuevo a "/add"
     * porque el repositorio de Spring, al hacer repo.save(), va a generar un usario
     * nuevo si no
     * se proporciona ID y actualizará uno existente si el objeto tiene ID.
     */
    @GetMapping("/edit/{id}")
    public String editUsuarioForm(Model modelo, @PathVariable("id") @NonNull Long id) {
        Optional<Usuario> oUsuario = repoUsuario.findById(id);
        if (oUsuario.isPresent()) {
            modelo.addAttribute(
                    "usuario", oUsuario.get());
            modelo.addAttribute(
                    "listaRoles", Rol.values());
            List<Rol> usuarioRoles = oUsuario.get()
                .getRoles().stream()
                .map(RolUsuario::getRol).collect(Collectors.toList());
            modelo.addAttribute("usuarioRoles", usuarioRoles);
            return "admin/usuarios/edit";
        } else {
            modelo.addAttribute("mensaje", "El usuario consultado no existe.");
            return "error";
        }
    }

    /**
     * Muestra el formulario para añadir un teléfono al usuario dado
     */
    @GetMapping("/{id}/telefonos/add")
    public String usuarioAddPhoneForm(
            @PathVariable @NonNull Long id,
            Model modelo) {

        Optional<Usuario> oUsuario = repoUsuario.findById(id);

        if (!oUsuario.isPresent()) {
            modelo.addAttribute(
                    "mensaje", "El usuario no existe");
            return "error";
        }

        // modelo.addAttribute(
        // "usuario", oUsuario.get());
        // modelo.addAttribute(
        // "telefonos", oUsuario.get().getTelefonos());

        Telefono telefono = new Telefono();
        telefono.setUsuario(oUsuario.get());

        modelo.addAttribute(
                "telefono", telefono);
        modelo.addAttribute("usuario", oUsuario.get());

        return "admin/usuarios/telefonos/add";
    }

    /**
     * Añade un teléfono al usuario proporcionado
     */
    @PostMapping("/{id}/telefonos/add")
    public String usuarioAddPhone(
            @PathVariable @NonNull Long id,
            @ModelAttribute("telefono") @NonNull Telefono telefono,
            Model modelo) {

        Optional<Usuario> oUsuario = repoUsuario.findById(id);

        if (!oUsuario.isPresent()) {
            modelo.addAttribute(
                    "mensaje", "El usuario no existe");
            return "error";
        }

        // telefono.setUsuario(oUsuario.get());

        repoTelefono.save(telefono);

        return "redirect:/admin/usuarios/"+id+"/telefonos";
    }

    @GetMapping("/{id}/telefonos")
    public String getPhonesByUserId(
            @PathVariable @NonNull Long id,
            Model modelo) {

        Optional<Usuario> oUsuario = repoUsuario.findById(id);

        if (!oUsuario.isPresent()) {
            modelo.addAttribute(
                    "mensaje", "El usuario no existe");
            return "error";
        }

        modelo.addAttribute(
                "usuarios", repoUsuario.findAll());
        modelo.addAttribute(
                "usuarioActual", oUsuario.get());
        modelo.addAttribute(
                "telefonos", oUsuario.get().getTelefonos());

        // modelo.addAttribute(
        // "telefono", new Telefono());

        return "admin/usuarios/telefonos/telefonos";
    }

    @GetMapping("/{idUser}/telefonos/{idTel}/edit")
    public String editPhonesByUserIdForm(
            @PathVariable @NonNull Long idUser,
            @PathVariable @NonNull Long idTel,
            Model modelo) {

        Optional<Usuario> oUsuario = repoUsuario.findById(idUser);
        Optional<Telefono> oTelefono = repoTelefono.findById(idTel);

        if (!oUsuario.isPresent() ||
                !oTelefono.isPresent()) {
            modelo.addAttribute(
                    "mensaje", "El teléfono o usuario no existen");
            return "error";
        }

        modelo.addAttribute(
                "usuario", oUsuario.get());
        // modelo.addAttribute(
        // "telefonos", oUsuario.get().getTelefonos());

        modelo.addAttribute(
                "telefono", oTelefono.get());

        return "admin/usuarios/telefonos/edit";
    }

    /**
     * Este endpoint recoge el teléfono a actualizar
     * @param idUser
     * @param idTel
     * @param telefono
     * @param modelo
     * @return
     */
    @PostMapping("/{idUser}/telefonos/{idTel}/edit")
    public String editPhonesByUserId(
            @PathVariable @NonNull Long idUser,
            @PathVariable @NonNull Long idTel,
            @ModelAttribute("telefono") @NonNull Telefono telefono,
            Model modelo) {

        Optional<Usuario> oUsuario = repoUsuario.findById(idUser);
        Optional<Telefono> oTelefono = repoTelefono.findById(idTel);

        if (!oUsuario.isPresent() ||
                !oTelefono.isPresent()) {
            modelo.addAttribute(
                    "mensaje", "El teléfono o usuario no existen");
            return "error";
        }
        telefono.setId(idTel);
        telefono.setUsuario(oUsuario.get());
        repoTelefono.save(telefono);

        return "redirect:/admin/usuarios/"+idUser+"/telefonos";
    }

    @GetMapping("/{idUser}/telefonos/{idTel}/delete")
    public String deletePhonesByUserIdForm(
            @PathVariable @NonNull Long idUser,
            @PathVariable @NonNull Long idTel,
            Model modelo) {

        Optional<Usuario> oUsuario = repoUsuario.findById(idUser);
        Optional<Telefono> oTelefono = repoTelefono.findById(idTel);

        if (!oUsuario.isPresent() ||
                !oTelefono.isPresent()) {
            modelo.addAttribute(
                    "mensaje", "El teléfono o usuario no existen");
            return "error";
        }

        modelo.addAttribute(
                "usuario", oUsuario.get());
        // modelo.addAttribute(
        // "telefonos", oUsuario.get().getTelefonos());

        modelo.addAttribute(
                "telefono", oTelefono.get());

        return "admin/usuarios/telefonos/delete";
    }

    @PostMapping("/{idUser}/telefonos/{idTel}/delete")
    public String deletePhonesByUserId(
            @PathVariable @NonNull Long idUser,
            @PathVariable @NonNull Long idTel,
            Model modelo) {

        Optional<Usuario> oUsuario = repoUsuario.findById(idUser);
        Optional<Telefono> oTelefono = repoTelefono.findById(idTel);

        if (!oUsuario.isPresent() ||
                !oTelefono.isPresent()) {
            modelo.addAttribute(
                    "mensaje", "El teléfono o usuario no existen");
            return "error";
        }

        if (oUsuario.get().getId() != oTelefono.get().getUsuario().getId()) {
            modelo.addAttribute(
                    "mensaje", "El teléfono no pertenece al usuario");
            return "error";
        }

        
        repoTelefono.delete(oTelefono.get());

        return "redirect:/admin/usuarios/" + oUsuario.get().getId() + "/telefonos";
    }

    
    /**
     * Muestra el formulario para añadir un teléfono al usuario dado
     */
    @GetMapping("/{id}/direcciones/add")
    public String usuarioAddAddressForm(
            @PathVariable @NonNull Long id,
            Model modelo) {

        Optional<Usuario> oUsuario = repoUsuario.findById(id);

        if (!oUsuario.isPresent()) {
            modelo.addAttribute(
                    "mensaje", "El usuario no existe");
            return "error";
        }

        Direccion direccion = new Direccion();
        direccion.setUsuario(oUsuario.get());

        modelo.addAttribute(
                "direccion", direccion);
        modelo.addAttribute("usuario", oUsuario.get());

        return "admin/usuarios/direcciones/add";
    }

    /**
     * Añade un teléfono al usuario proporcionado
     */
    @PostMapping("/{id}/direcciones/add")
    public String usuarioAddAddress(
            @PathVariable @NonNull Long id,
            @ModelAttribute("direccion") @NonNull Direccion direccion,
            Model modelo) {

        Optional<Usuario> oUsuario = repoUsuario.findById(id);

        if (!oUsuario.isPresent()) {
            modelo.addAttribute(
                    "mensaje", "El usuario no existe");
            return "error";
        }

        // telefono.setUsuario(oUsuario.get());
        direccion.setUsuario(oUsuario.get());
        repoDireccion.save(direccion);

        return "redirect:/admin/usuarios/"+id+"/direcciones";
    }

    @GetMapping("/{id}/direcciones")
    public String getAddressByUserId(
            @PathVariable @NonNull Long id,
            Model modelo) {

        Optional<Usuario> oUsuario = repoUsuario.findById(id);

        if (!oUsuario.isPresent()) {
            modelo.addAttribute(
                    "mensaje", "El usuario no existe");
            return "error";
        }

        modelo.addAttribute(
                "usuarios", repoUsuario.findAll());
        modelo.addAttribute(
                "usuarioActual", oUsuario.get());
        modelo.addAttribute(
                "direcciones", oUsuario.get().getDirecciones());

        // modelo.addAttribute(
        // "telefono", new Telefono());

        return "admin/usuarios/direcciones/direcciones";
    }

    @GetMapping("/{idUser}/direcciones/{idTel}/edit")
    public String editAddressByUserIdForm(
            @PathVariable @NonNull Long idUser,
            @PathVariable @NonNull Long idTel,
            Model modelo) {

        Optional<Usuario> oUsuario = repoUsuario.findById(idUser);
        Optional<Telefono> oTelefono = repoTelefono.findById(idTel);

        if (!oUsuario.isPresent() ||
                !oTelefono.isPresent()) {
            modelo.addAttribute(
                    "mensaje", "El teléfono o usuario no existen");
            return "error";
        }

        modelo.addAttribute(
                "usuario", oUsuario.get());
        // modelo.addAttribute(
        // "telefonos", oUsuario.get().getTelefonos());

        modelo.addAttribute(
                "telefono", oTelefono.get());

        return "admin/usuarios/telefonos/edit";
    }

    /**
     * Este endpoint recoge el teléfono a actualizar
     * @param idUser
     * @param idTel
     * @param telefono
     * @param modelo
     * @return
     */
    @PostMapping("/{idUser}/direcciones/{idTel}/edit")
    public String editAddressByUserId(
            @PathVariable @NonNull Long idUser,
            @PathVariable @NonNull Long idTel,
            @ModelAttribute("telefono") @NonNull Telefono telefono,
            Model modelo) {

        Optional<Usuario> oUsuario = repoUsuario.findById(idUser);
        Optional<Telefono> oTelefono = repoTelefono.findById(idTel);

        if (!oUsuario.isPresent() ||
                !oTelefono.isPresent()) {
            modelo.addAttribute(
                    "mensaje", "El teléfono o usuario no existen");
            return "error";
        }
        telefono.setId(idTel);
        telefono.setUsuario(oUsuario.get());
        repoTelefono.save(telefono);

        return "redirect:/admin/usuarios/"+idUser+"/direcciones";
    }

    @GetMapping("/{idUser}/direcciones/{idTel}/delete")
    public String deleteAddressByUserIdForm(
            @PathVariable @NonNull Long idUser,
            @PathVariable @NonNull Long idTel,
            Model modelo) {

        Optional<Usuario> oUsuario = repoUsuario.findById(idUser);
        Optional<Telefono> oTelefono = repoTelefono.findById(idTel);

        if (!oUsuario.isPresent() ||
                !oTelefono.isPresent()) {
            modelo.addAttribute(
                    "mensaje", "El teléfono o usuario no existen");
            return "error";
        }

        modelo.addAttribute(
                "usuario", oUsuario.get());
        // modelo.addAttribute(
        // "telefonos", oUsuario.get().getTelefonos());

        modelo.addAttribute(
                "telefono", oTelefono.get());

        return "admin/usuarios/direcciones/delete";
    }

    @PostMapping("/{idUser}/direcciones/{idTel}/delete")
    public String deleteAddressByUserId(
            @PathVariable @NonNull Long idUser,
            @PathVariable @NonNull Long idTel,
            Model modelo) {

        Optional<Usuario> oUsuario = repoUsuario.findById(idUser);
        Optional<Telefono> oTelefono = repoTelefono.findById(idTel);

        if (!oUsuario.isPresent() ||
                !oTelefono.isPresent()) {
            modelo.addAttribute(
                    "mensaje", "El teléfono o usuario no existen");
            return "error";
        }

        if (oUsuario.get().getId() != oTelefono.get().getUsuario().getId()) {
            modelo.addAttribute(
                    "mensaje", "El teléfono no pertenece al usuario");
            return "error";
        }

        
        repoTelefono.delete(oTelefono.get());

        return "redirect:/admin/usuarios/" + oUsuario.get().getId() + "/direcciones";
    }
}
