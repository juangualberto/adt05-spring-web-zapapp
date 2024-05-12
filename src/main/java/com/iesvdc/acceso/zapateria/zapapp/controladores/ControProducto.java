package com.iesvdc.acceso.zapateria.zapapp.controladores;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.iesvdc.acceso.zapateria.zapapp.modelos.Categoria;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Producto;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoCategoria;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoProducto;

import lombok.NonNull;


@Controller
@RequestMapping("/admin")
public class ControProducto {
    
    @Autowired
    RepoCategoria repoCategoria;
    @Autowired
    RepoProducto repoProducto;


    @GetMapping("producto")
    public String findAll(Model model) {
        model.addAttribute("productos", repoProducto.findAll());
        return "admin/productos";
    }

    @GetMapping("producto/categoria/{id}")
    public String findByCategoria(
            Model model, 
            @PathVariable(name = "id") Long id) {

        Optional<Categoria> oCategoria = repoCategoria.findById(id);        

        if(oCategoria.isPresent()) {
            Categoria padre = oCategoria.get();
            List <Categoria> lCategorias = repoCategoria.findAll();
            model.addAttribute("productos", repoProducto.findByCategoria(padre));
            model.addAttribute("categorias", lCategorias);
            model.addAttribute("categoria", padre);
            return "admin/productos-cat";
        } else {
            model.addAttribute("titulo", "Producto: ERROR");
            model.addAttribute("mensaje", "No puedo encontrar esa categoría en la base de datos");
            return "error";
        }

    }

    @GetMapping("producto/categoria")
    public String findByCategorias(Model model) {
        
        List <Categoria> lCategorias = repoCategoria.findAll();
        model.addAttribute("productos", repoProducto.findAll());
        model.addAttribute("categorias", lCategorias);

        return "admin/productos-cat";
    }

    @GetMapping("producto/add")
    public String addForm(Model modelo) {
        
        modelo.addAttribute("producto", new Producto());
        modelo.addAttribute("categorias", repoCategoria.findAll());
        return "admin/productos-add";
    }

    @PostMapping("producto/add")
    public String postMethodName(
        @ModelAttribute("producto") Producto producto)  {
        repoProducto.save(producto);
        return "redirect:/admin/producto";
    }
    
    @GetMapping("producto/delete/{id}")
    public String deleteForm(
            @PathVariable(name = "id") @NonNull Long id,
            Model modelo) {
        try {
            Optional<Producto> producto = repoProducto.findById(id);
            if (producto.isPresent()){
                // si existe la producto
                modelo.addAttribute(
                    "producto", producto.get());
                return "admin/productos-del";
            } else {
                return "error";
            }

        } catch (Exception e) {
            return "error";
        }
    }
    

    @PostMapping("producto/delete/{id}")
    public String delete(
            @PathVariable("id") @NonNull Long id) {
        try {
            repoProducto.deleteById(id);    
        } catch (Exception e) {
            return "error";
        }
        
        return "redirect:/admin/producto";
    }


    @GetMapping("producto/edit/{id}")
    public String editForm(
        @PathVariable @NonNull Long id,
        Model modelo) {

            Optional<Producto> producto = repoProducto.findById(id);
            List<Categoria> categorias = repoCategoria.findAll();
            if (producto.isPresent()){
                modelo.addAttribute("producto", producto.get());
                modelo.addAttribute("categorias", categorias);
                return "admin/productos-edit";
            } else {
                modelo.addAttribute(
                    "mensaje", 
                    "Producto no encontrada");
                modelo.addAttribute(
                    "titulo", 
                    "Error en productos.");
                return "error";
            }            
    }
    
}
