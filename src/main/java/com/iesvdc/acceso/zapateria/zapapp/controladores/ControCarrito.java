package com.iesvdc.acceso.zapateria.zapapp.controladores;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.iesvdc.acceso.zapateria.zapapp.excepciones.CarroException;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Estado;
import com.iesvdc.acceso.zapateria.zapapp.modelos.LineaPedido;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Pedido;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Producto;
import com.iesvdc.acceso.zapateria.zapapp.modelos.Usuario;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoDireccion;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoLineaPedido;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoPedido;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoProducto;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoTelefono;
import com.iesvdc.acceso.zapateria.zapapp.repositorios.RepoUsuario;

import jakarta.transaction.Transactional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Esta clase implementa el servicio de carro de la compra
 */
@Controller
public class ControCarrito {
    
    @Autowired
    private RepoProducto repoProducto;

    @Autowired
    private RepoPedido repoPedido;

    @Autowired
    private RepoUsuario repoUsuario;

    @Autowired 
    private RepoLineaPedido repoLineaPedido;

    @Autowired
    private RepoDireccion repoDireccion;

    @Autowired
    private RepoTelefono repoTelefono;

    /**
     * Este método obtiene, del contexto de la aplicación, información sobre la autenticación.
     * Devuelve un objeto de tipo Usuario que es además quien ha entrado en la aplicación.
     * @return Usuario 
     */
    private Usuario getLoggedUser() {
        // Del contexto de la aplicación obtenemos el usuario
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        // obtenemos el usuario del repositorio por su "username"
        return repoUsuario.findByUsername(username).get(0);
    }

    /**
     * End-point "/productos"
     * Este controlador devuelve al hacer GET /productos
     * la vista con todos los zapatos de la BBDD para poder 
     * añadirlos al carrito de la compra.
     * Este controlador usa un modelo llamado "productos" para 
     * que la vista dibuje una tabla con todos los productos.
     * Actualmente no hace paginación aún.
     * @param modelo
     * @return la vista de productos que hay en el carro.
     */
    @GetMapping("/productos")
    public String findAll(Model modelo) {
        List<Producto> productos = repoProducto.findAll();
        productos.removeIf(producto -> producto.getStock()==0);
        modelo.addAttribute(
            "productos", 
            productos);
        return "carro/productos";
    }
    
    /**
     * Este método muestra el carro de la compra para el usuario que hizo login.
     * Un carro de la compra es un pedido en un estado especial que aún no tiene 
     * fecha, ni hora, ni otros datos.
     * Este controlador añade los datos del carro de la compra a la vista.
     * @param modelo
     * @return el carro de la compra con los productos que se hayan metido (si existen)
     */
    @GetMapping("/carro")
    public String findCarro(Model modelo) {

        List<LineaPedido> lineaPedidos = null;

        Usuario cliente = getLoggedUser();

        long total = 0;
        
        // Para el usuario que hizo login, buscamos un pedido (sólo puede haber uno) en estado "CARRITO"
        List<Pedido> pedidos = repoPedido.findByEstadoAndCliente(Estado.CARRITO, cliente);
        if (pedidos.size()>0) {
            lineaPedidos = repoLineaPedido.findByPedido(pedidos.get(0));
            for (LineaPedido lp : pedidos.get(0).getLineaPedidos()) {
                total += lp.getCantidad()*lp.getProducto().getPrecio();
            }
        }
        

        // mandamos a la vista los modelos: Pedido y su lista de LineaPedido             
        modelo.addAttribute("lineapedidos", lineaPedidos);
        modelo.addAttribute("total", total);

        // modelo.addAttribute("productos", productos );
        return "carro/carro";
    }
    

    /**
     * Muestra el formulario para añadir el producto al carro de la compra del usuario 
     * que ha hecho login.
     * Un carro de la compra es un pedido en un estado especial que aún no tiene 
     * fecha, ni hora, ni otros datos.
     * @param id El ID del producto que muestra el formulario para añadir a la cesta
     * @return la vista para aceptar añadir al carro de la compra.
     */
    @GetMapping("/carro/add/{id}")
    public String addForm(
        @PathVariable @NonNull Long id, Model modelo) {

        Optional <Producto> producto = repoProducto.findById(id);        

        if (producto.isPresent()){ 
            // Si el producto está ya en el carro, haremos un "edit"
            List<Pedido> pedidos = repoPedido.findByEstadoAndCliente(Estado.CARRITO, getLoggedUser());
            if (pedidos.size()>0) {
                Pedido carro = pedidos.get(0);
                for (LineaPedido lp : carro.getLineaPedidos()) {
                    if(lp.getProducto().getId()==id) {
                        modelo.addAttribute("lineaPedido", lp);
                        modelo.addAttribute("producto", lp.getProducto());
                        modelo.addAttribute("cantidad", lp.getCantidad());
                        return "carro/carro-edit";                        
                    }
                }
            }
            modelo.addAttribute("producto", producto.get());            
        } else {
            modelo.addAttribute("titulo", "Error al añadir al carrito");
            modelo.addAttribute("mensaje", "No se ha podido encontrar ese producto en la base de datos");
            return "error";
        } 
        return "carro/carro-add";
    }


     /**
     * Este método añade realmente un producto al carro de la compra
     * Un carro de la compra es un pedido en un estado especial que aún no tiene 
     * fecha, ni hora, ni otros datos.
     * Si no existe ese pedido especial "carro de la compra" es aquí y ahora donde 
     * se crea para añadirle el detalle de pedido que es el producto del formulario anterior.
     * @param id El ID del producto que muestra el formulario para añadir a la cesta
     * @return la vista para aceptar añadir al carro de la compra
     */
    @PostMapping("/carro/add/{id}")
    public String add(
        @PathVariable @NonNull Long id, 
        @RequestParam @NonNull Integer cantidad,
        Model modelo) {

        String vista = "redirect:/carro";
        Optional <Producto> producto = repoProducto.findById(id);        
        Usuario cliente = getLoggedUser();
        Pedido carrito ;

        List<Pedido> pedidos = repoPedido.findByEstadoAndCliente(Estado.CARRITO, cliente);

        // ahora añadimos una línea al pedido        
        if (producto.isPresent() && cantidad>0 ){ 
            // si no existe carro de la compra se crea
            if (pedidos.size()>0) {
                carrito = pedidos.get(0);
            } else {
                carrito = new Pedido();
                carrito.setCliente(cliente);
                carrito.setEstado(Estado.CARRITO);
                carrito = repoPedido.save(carrito);
            }
            LineaPedido lineaPedido = new LineaPedido();

            if (carrito.getLineaPedidos()!= null) {
                // TEST para ver si ya estaba en el carro el producto
                for (LineaPedido lp : carrito.getLineaPedidos()) {
                    if(lp.getProducto().getId()==id) {
                        cantidad = lp.getCantidad()+cantidad;
                        lineaPedido = lp;
                    }
                }
            }

            // TEST para ver si queda stock
            if (cantidad <= producto.get().getStock()) {                
                lineaPedido.setProducto(producto.get());
                lineaPedido.setCantidad(cantidad);
                lineaPedido.setPedido(carrito);
                lineaPedido = repoLineaPedido.save(lineaPedido);
            } else {
                modelo.addAttribute("titulo", "Error al añadir " + producto.get().getNombre() + " al carrito");
                modelo.addAttribute("mensaje", "No hay suficiente stock (quedan " + producto.get().getStock() + " unidades).");  
                vista = "error";
            }
        } else {
            modelo.addAttribute("titulo", "Error al añadir al carrito");
            modelo.addAttribute("mensaje", "No se ha podido encontrar ese producto en la base de datos");
            vista = "error";
        }

        return vista;
    }

    /**
     * Muestra el formulario para editar la cantidad de un producto del carro 
     * de la compra del usuario que ha hecho login.
     * Necesitamos comprobar que esa lineaPedido pertenece a ese cliente.
     * @param id El ID de la lineaPedido que muestra el formulario para añadir 
     * a la cesta
     * @return la vista para aceptar añadir al carro de la compra.
     */
    @GetMapping("/carro/edit/{id}")
    public String editForm(
         @PathVariable @NonNull Long id, Model modelo) {
        
        String vista;
        
        Optional <LineaPedido> lp = repoLineaPedido.findById(id);
        if (lp.isPresent()){ 
            // comprobamos que el pedido pertenece al usuario que hizo login
            // sin esta comprobación, ¡¡podríamos ver productos en carros de otros usuarios!!
            List<LineaPedido> lineaPedidos = 
                repoLineaPedido.lineaPedidoBelongsToUser(lp.get(), getLoggedUser());
            if (lineaPedidos.size()>0){
                modelo.addAttribute("lineaPedido", lineaPedidos.get(0));
                modelo.addAttribute("producto", lp.get().getProducto());
                modelo.addAttribute("cantidad", lp.get().getCantidad());
                vista = "carro/carro-edit";
            } else {
                modelo.addAttribute("titulo", "Error al editar productos del carrito");
                modelo.addAttribute("mensaje", "No se ha podido encontrar ese producto en su carro");
                vista = "error";
            }
        } else {
            modelo.addAttribute("titulo", "Error al añadir al carrito");
            modelo.addAttribute("mensaje", "No se ha podido encontrar ese producto en la base de datos");
            vista = "error";
        }
        
        return vista;
    }

    @PostMapping("/carro/edit")
    public String edit(
        @ModelAttribute("lineaPedido") @NonNull LineaPedido lineaPedido, 
        Model modelo) {
        
        String vista = "redirect:/carro";
    
        // Aunque nos llega el objeto por si nos manipulan el mismo en el formulario, lo buscamos por ID
        Optional <LineaPedido> lp = repoLineaPedido.findById(lineaPedido.getId());        

        if (lp.isPresent()){ 
            LineaPedido oldLineaPedido = lp.get();
            // comprobamos que el pedido pertenece al usuario que hizo login
            // sin esta comprobación, ¡¡podríamos ver productos en carros de otros usuarios!!
            List<LineaPedido> lineaPedidos = repoLineaPedido.lineaPedidoBelongsToUser(lineaPedido, getLoggedUser());
            if (lineaPedidos.size()>0 && oldLineaPedido.getProducto().getStock()>=lineaPedido.getCantidad()){
                oldLineaPedido.setCantidad(lineaPedido.getCantidad());
                repoLineaPedido.save(oldLineaPedido);
            } else {
                modelo.addAttribute("titulo", "Error al editar productos del carrito");
                modelo.addAttribute("mensaje", "Sin stock para poder hacer el cambio");
                vista = "error";
            }
        } else {
            modelo.addAttribute("titulo", "Error al añadir al carrito");
            modelo.addAttribute("mensaje", "No se ha podido encontrar ese producto en la base de datos");
            vista = "error";
        }
        
        return vista;
        
    }
    
    /**
     * Muestra el formulario para quitar un producto del carro de la compra del usuario 
     * que ha hecho login.
     * Necesitamos comprobar que esa lineaPedido pertenece a ese cliente.
     * @param id El ID de la lineaPedido que muestra el formulario para añadir a la cesta
     * @return la vista para aceptar añadir al carro de la compra.
     */
    @GetMapping("/carro/del/{id}")
    public String delForm(
        @PathVariable @NonNull Long id, Model modelo) {
        
        String vista;
        
        Optional <LineaPedido> lp = repoLineaPedido.findById(id);
        if (lp.isPresent()){ 
            // comprobamos que el pedido pertenece al usuario que hizo login
            // sin esta comprobación, ¡¡podríamos ver productos en carros de otros usuarios!!
            List<LineaPedido> lineaPedidos = repoLineaPedido.lineaPedidoBelongsToUser(lp.get(), getLoggedUser());
            if (lineaPedidos.size()>0){
                modelo.addAttribute("lineaPedido", lineaPedidos.get(0));                
                vista = "carro/carro-del";
            } else {
                modelo.addAttribute("titulo", "Error al borrar productos del carrito");
                modelo.addAttribute("mensaje", "No se ha podido encontrar ese producto en su carro");
                vista = "error";
            }
        } else {
            modelo.addAttribute("titulo", "Error al borrar del carrito");
            modelo.addAttribute("mensaje", "No se ha podido encontrar ese producto en la base de datos");
            vista = "error";
        }
        
        return vista;
    }

    /**
     * Muestra el formulario para quitar un producto del carro de la compra del usuario 
     * que ha hecho login.
     * Necesitamos comprobar que esa lineaPedido pertenece a ese cliente.
     * @param id El ID de la lineaPedido que muestra el formulario para añadir a la cesta
     * @return la vista para aceptar añadir al carro de la compra.
     */
    @PostMapping("/carro/del")
    public String delete(
        @RequestParam @NonNull Long id, Model modelo) {
        
        String vista;
        
        Optional <LineaPedido> lp = repoLineaPedido.findById(id);
        if (lp.isPresent()){ 
            // comprobamos que el pedido pertenece al usuario que hizo login
            // sin esta comprobación, ¡¡podríamos ver productos en carros de otros usuarios!!
            List<LineaPedido> lineaPedidos = repoLineaPedido.lineaPedidoBelongsToUser(lp.get(), getLoggedUser());
            if (lineaPedidos.size()>0){
                Pedido carro = lineaPedidos.get(0).getPedido();
                repoLineaPedido.delete(lineaPedidos.get(0));
                // si es el último producto del carro borramos el carro
                if (carro.getLineaPedidos().size()==0)
                    repoPedido.delete(carro);
                
                vista = "redirect:/carro";
            } else {
                modelo.addAttribute("titulo", "Error al borrar productos del carrito");
                modelo.addAttribute("mensaje", "No se ha podido encontrar ese producto en su carro");
                vista = "error";
            }
        } else {
            modelo.addAttribute("titulo", "Error al borrar del carrito");
            modelo.addAttribute("mensaje", "No se ha podido encontrar ese producto en la base de datos");
            vista = "error";
        }
        
        return vista;
    }


    @GetMapping("/carro/confirmar")
    public String confirmForm(Model modelo) {

        Usuario loggedUser = getLoggedUser();
        long total = 0;

        // Para el usuario que hizo login, buscamos el pedido (sólo puede haber uno) en estado "CARRITO"
        List<Pedido> pedidos = repoPedido.findByEstadoAndCliente(Estado.CARRITO, loggedUser);
        if (pedidos.size()==1) {
            modelo.addAttribute("pedido", pedidos.get(0));            
            modelo.addAttribute("direcciones", loggedUser.getDirecciones());
            modelo.addAttribute("telefonos", loggedUser.getTelefonos());

            for (LineaPedido lp : pedidos.get(0).getLineaPedidos()) {
                total += lp.getCantidad()*lp.getProducto().getPrecio();
            }
            modelo.addAttribute("total", total);
            
            return "carro/carro-confirm";   
        } else {
            modelo.addAttribute("titulo", "Error al confirmar el pedido");
            modelo.addAttribute("mensaje", "No se ha podido encontrar ese pedido en la base de datos");
            return "error";
        }
    }
    
    @PostMapping("/carro/confirmar")
    @Transactional(rollbackOn = CarroException.class)
    public String confirm(
        @ModelAttribute("lineaPedido") @NonNull Pedido pedido,
        Model modelo) throws CarroException {

        Usuario loggedUser = getLoggedUser();
        long total = 0;            

        // Para el usuario que hizo login, buscamos el pedido (sólo puede haber uno) en estado "CARRITO"
        List<Pedido> pedidos = repoPedido.findByEstadoAndCliente(Estado.CARRITO, loggedUser);
        if (pedidos.size()==1 ) {
            if(pedidos.get(0).getId()==pedido.getId()) {
                pedido.setCliente(loggedUser);
                pedido.setDescuento(Float.valueOf(0));
                pedido.setEstado(Estado.REALIZADO);
                pedido.setFecha(LocalDate.now());                
                for (LineaPedido lp : pedidos.get(0).getLineaPedidos()) {
                    // comprobamos si hay stock
                    Producto p = lp.getProducto();
                    if (p.getStock()>=lp.getCantidad()) {
                        lp.setPrecio(lp.getProducto().getPrecio());
                        total += lp.getCantidad()*lp.getProducto().getPrecio();
                        p.setStock(p.getStock()-lp.getCantidad());
                        repoProducto.save(p);
                    } else {
                        throw new CarroException(
                            "No queda suficiente stock de: " + 
                            p.getNombre() + 
                            " para completar el pedido. Sólo quedan: " + 
                            p.getStock()+
                            " unidades y en el pedido se solicitan: " + 
                            lp.getCantidad() + 
                            ". Intente poner menos unidades para completar el pedido.");
                    }
                }
                pedido.setTotal(Float.valueOf(total));
                repoPedido.save(pedido);
            } else {
                modelo.addAttribute("titulo", "Error al confirmar el pedido");
                modelo.addAttribute("mensaje", "Los datos del pedido no son válidos.");
                return "error";
            }
        } else {
            modelo.addAttribute("titulo", "Error al confirmar el pedido");
            modelo.addAttribute("mensaje", "No se ha podido encontrar ese pedido en la base de datos");
            return "error";
        }

        return "redirect:/mis-pedidos";
    }

    @GetMapping("/mis-pedidos")
    public String getPedidos(Model modelo) {
        List<Pedido> pedidos = repoPedido.findByCliente(getLoggedUser());
        // quitamos el carro de la compra de la lista
        pedidos.removeIf( x -> x.getEstado() == Estado.CARRITO);
        modelo.addAttribute("pedidos", pedidos);
        if (pedidos.size()>0)
            return "pedido/pedidos";
        else {
            modelo.addAttribute("titulo", "Error al mostrar sus pedidos");
            modelo.addAttribute("mensaje", "No se ha podido encontrar ningún pedido en la base de datos");            
            return "error";
        }
    }
    
    @GetMapping("/mis-pedidos/{id}")
    public String detallePedido(
        @PathVariable @NonNull Long id,
        Model modelo) {
        String vista = "pedido/detalle";
        Optional<Pedido> oPedido = repoPedido.findById(id);
        if (oPedido.isPresent()){
            modelo.addAttribute("pedido", oPedido.get());
            modelo.addAttribute("lineaPedidos", oPedido.get().getLineaPedidos());
        }
        else { 
            modelo.addAttribute("titulo", "Error al mostrar pedidos");
            modelo.addAttribute("mensaje", "No se ha podido encontrar ese pedido en la base de datos");
            vista = "error";
        }
        return vista;
    }   

}
