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
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoCategoria;

import lombok.NonNull;


@Controller
@RequestMapping("/admin")
public class ControCategoria {
    
    @Autowired
    RepoCategoria repoCategoria;


    @GetMapping("categoria")
    public String findAll(Model model) {
        model.addAttribute("categorias", repoCategoria.findAll());
        return "admin/categorias";
    }

    @GetMapping("categoria/hijos/{id}")
    public String findChilds(
            Model model, 
            @PathVariable(name = "id") Long id) {

        Optional<Categoria> oCategoria = repoCategoria.findById(id);
        
        if(oCategoria.isPresent()) {
            Categoria padre = oCategoria.get();
            model.addAttribute("categorias", repoCategoria.findByPadre(padre));
            return "admin/categorias";
        } else {
            model.addAttribute("titulo", "Categoria: ERROR");
            model.addAttribute("mensaje", "No puedo encontrar esa categoría en la base de datos");
            return "error";
        }

    }

    @GetMapping("categoria/add")
    public String addForm(Model modelo) {
        modelo.addAttribute("categorias", repoCategoria.findAll());
        modelo.addAttribute("categoria", new Categoria());
        return "admin/categorias-add";
    }

    @PostMapping("categoria/add")
    public String postMethodName(
        @ModelAttribute("categoria") Categoria categoria)  {
        repoCategoria.save(categoria);
        return "redirect:/admin/categoria";
    }
    
    @GetMapping("categoria/delete/{id}")
    public String deleteForm(
            @PathVariable(name = "id") @NonNull Long id,
            Model modelo) {
        try {
            Optional<Categoria> categoria = repoCategoria.findById(id);
            if (categoria.isPresent()){
                // si existe la categoria
                modelo.addAttribute(
                    "categoria", categoria.get());
                return "admin/categorias-del";
            } else {
                return "error";
            }

        } catch (Exception e) {
            return "error";
        }
    }
    

    @PostMapping("categoria/delete/{id}")
    public String delete(
            @PathVariable("id") @NonNull Long id) {
        try {
            repoCategoria.deleteById(id);    
        } catch (Exception e) {
            return "error";
        }
        
        return "redirect:/admin/categoria";
    }


    @GetMapping("categoria/edit/{id}")
    public String editForm(
        @PathVariable @NonNull Long id,
        Model modelo) {

            Optional<Categoria> categoria = 
                repoCategoria.findById(id);
            List<Categoria> categorias = 
                repoCategoria.findAll();
                
            if (categoria.isPresent()){
                modelo.addAttribute("categoria", categoria.get());
                modelo.addAttribute("categorias", categorias);
                return "admin/categorias-edit";
            } else {
                modelo.addAttribute(
                    "mensaje", 
                    "Categoria no encontrada");
                modelo.addAttribute(
                    "titulo", 
                    "Error en categorías.");
                return "error";
            }            
    }
    
}
