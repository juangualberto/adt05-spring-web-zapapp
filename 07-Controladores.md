# Controladores

Los controladores son componentes que se utilizan para manejar las solicitudes HTTP y generar las respuestas correspondientes. Actúan como intermediarios entre el cliente y el servidor, procesando las solicitudes entrantes y produciendo las respuestas adecuadas.

En el contexto de Spring MVC (Model-View-Controller), los controladores reciben las solicitudes HTTP, extraen los datos necesarios (consultan bases de datos, colecciones de documentos...), invocan la lógica de negocio apropiada y devuelven una respuesta al cliente. Se definen como clases anotadas con la anotación @Controller o @RestController, que les permite ser reconocidos y administrados por el contenedor de Spring.

Funcionalidades clave de los controladores en Spring:

1. **Gestión de rutas**: Los controladores definen métodos que se asocian a rutas o URLs específicas. Esto se logra mediante la anotación **@RequestMapping** en Spring MVC o mediante anotaciones más específicas como **@GetMapping**, **@PostMapping**, etc.
2. **Recepción de parámetros**: Los métodos de los controladores pueden recibir parámetros enviados en la solicitud HTTP, como parámetros de consulta, encabezados, datos de formulario o cuerpo de la solicitud. Estos parámetros se pueden vincular directamente a los parámetros del método del controlador utilizando anotaciones como **@RequestParam**, **@PathVariable**, **@RequestHeader**, etc.
3. **Lógica de negocio**: Los controladores son responsables de invocar la lógica de negocio adecuada para procesar la solicitud. Esto puede implicar la interacción con servicios, repositorios u otros componentes de la aplicación para realizar operaciones, procesar datos y preparar la respuesta.
4. **Generación de peticiones**: Los controladores devuelven objetos que representan la respuesta a enviar al cliente. Estos objetos pueden ser cadenas de texto, objetos **JSON**, **vistas** (templates) a renderizar, archivos, redirecciones, entre otros. La selección del tipo de respuesta se basa en la anotación del método del controlador y la configuración de Spring.
5. **Manejo de excepciones**: Los controladores también pueden manejar excepciones que se produzcan durante el procesamiento de la solicitud. Esto permite capturar errores, realizar acciones específicas (como devolver un código de estado HTTP adecuado o mostrar una página de error personalizada) y mantener un flujo controlado de la aplicación.

> Los controladores devuelven una cadena de caracteres que es el nombre del archivo de la vista, es decir si devuelvo la cadena  "index" quiere decir que en la carpeta **resources/templates** existe un archivo **index.html** que será el que se vea en el navegador al hacer la petición **/index**.

## Listado de rutas de nuestra aplicación


Para completar la aplicación, se definen una serie de end-points que serán completados en sucesivos sprints (un sprint en la metodología SCRUM de trabajo es una tarea que al completarla obtenemos un subproducto funcional).

### Servicio usuario

  RUTA          |VERBO| DATOS | COMENTARIOS
------------------------|-----|-------|--------------------
/usuario/telefonos | GET | nada | Muestra los tel. del usuario que ha hecho login
/usuario/telefonos/add | GET | nada | Muestra formulario para añadir tel. al usuario que ha hecho login
/usuario/telefonos/add | POST | body (tel) | Añade tel. al usuario que ha hecho login
/usuario/telefonos/delete/{tel_id} | GET | nada | Muestra formulario para borrar tel. al usuario que ha hecho login
/usuario/telefonos/delete/{tel_id}  | POST | body (tel) | Borra tel. al usuario que ha hecho login
/usuario/telefonos/update/{tel_id} | GET | nada | Muestra formulario para editar el tel. del usuario que ha hecho login
/usuario/telefonos/update/{tel_id}  | POST | body (tel) | Edita tel. del usuario que ha hecho login

### Servicio producto

Rutas del servicio para el rol GESTOR:

RUTA            | METODO | ROL | Observaciones
--------------------|--------|---------|-------------------------
/admin/producto | GET | gestor | Mostrar listado productos
/admin/producto/create | GET | gestor | Mostrar formulario alta productos
/admin/producto/create | POST | gestor | Crear en la BBDD el producto
/admin/producto/update | GET | gestor | Mostrar formulario modificación productos
/admin/producto/update | POST | gestor | Modificar en la BBDD el producto
/admin/producto/delete | GET | gestor | Mostrar formulario borrar productos
/admin/producto/delete | POST | gestor | Borrar en la BBDD el producto
/admin/categoria | GET | gestor | Mostrar listado de categorías
/admin/categoria/create | GET | gestor | Mostrar formulario alta categoria
/admin/categoria/create | POST | gestor | Crear reserva en la BBDD
/admin/categoria/delete | GET | gestor | Mostrar formulario borrar categoria
/admin/categoria/delete | POST | gestor | Borra categoría de la BBDD
/admin/categoria/:id/producto | GET | gestor | Mostrar los productos de una categoría
/admin/categoria/producto | POST | gestor | Mostrar maestro-detalle de categoria para un usuario
/admin/categoria/producto/:id | GET | gestor | Mostrar categoria para ese producto
/admin/categoria/producto | POST | gestor | Mostrar maestro-detalle de categoria para una categoría

Para saber cómo añadir seguridad a estos endpoints, es decir, que sólo usuarios con el rol "GESTOR" puedan verlos, avanza al apartado de **"Seguridad"**.

Para saber cómo hacer las vistas gestionadas por estos controladores, avanza a **"Vistas"**.

***Archivo ControCategoria.java***

```java 

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

```

Este controlador Spring MVC maneja las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para la entidad `Categoria` y en él tenemos:

1. **Anotaciones en el controlador**:
   - `@Controller`: Indica que esta clase es un controlador de Spring MVC.
   - `@RequestMapping("/admin")`: Especifica el prefijo de la URL para todas las solicitudes manejadas por este controlador.

2. **Inyección de dependencias**:
   - `@Autowired RepoCategoria repoCategoria;`: Inyecta automáticamente una instancia de `RepoCategoria` en este controlador. `RepoCategoria` es un repositorio de Spring Data JPA que se utiliza para interactuar con la base de datos para la entidad `Categoria`.

3. **Método `findAll`**:
   - `@GetMapping("categoria")`: Maneja las solicitudes GET en la URL `/admin/categoria`.
   - `repoCategoria.findAll()`: Recupera todas las categorías de la base de datos y las agrega al modelo.
   - Retorna la vista `admin/categorias`.

4. **Método `findChilds`**:
   - `@GetMapping("categoria/hijos/{id}")`: Maneja las solicitudes GET en la URL `/admin/categoria/hijos/{id}` donde `{id}` es el ID de la categoría padre.
   - `repoCategoria.findById(id)`: Busca la categoría padre por su ID.
   - `repoCategoria.findByPadre(padre)`: Recupera todas las categorías hijas de la categoría padre y las agrega al modelo.
   - Retorna la vista `admin/categorias`.
   - Si no se encuentra la categoría padre, retorna una vista de error.

5. **Métodos `addForm` y `postMethodName`**:
   - `addForm` maneja las solicitudes GET para mostrar el formulario de agregar una nueva categoría.
   - `postMethodName` maneja las solicitudes POST para procesar el formulario de agregar una nueva categoría.
   - `@ModelAttribute("categoria") Categoria categoria`: Enlaza los datos enviados desde el formulario a un objeto `Categoria` automáticamente.

6. **Métodos `deleteForm` y `delete`**:
   - `deleteForm` maneja las solicitudes GET para mostrar el formulario de confirmación de eliminación de una categoría.
   - `delete` maneja las solicitudes POST para eliminar una categoría por su ID.
   - Si la categoría no se encuentra o si ocurre un error durante la eliminación, se muestra una vista de error.

7. **Método `editForm`**:
   - `editForm` maneja las solicitudes GET para mostrar el formulario de edición de una categoría.
   - Si la categoría no se encuentra, se muestra una vista de error.

Fíjate que no hay un método ni ruta para el POST del formulario editar. Resulta que si una categoría existe en la base de datos se actualizará y si no existe, se crea una nuevo. Esto lo sabemos si el **id** del categoría tiene un valor o si es **null** se trata de una categoría nueva.

***Archivo ControProducto.java***

```java 

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
        modelo.addAttribute("productos", repoProducto.findAll());
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

            Optional<Producto> producto = 
                repoProducto.findById(id);
            List<Producto> productos = 
                repoProducto.findAll();
                
            if (producto.isPresent()){
                modelo.addAttribute("producto", producto.get());
                modelo.addAttribute("productos", productos);
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

```

Este otro controlador Spring MVC maneja las operaciones CRUD para la entidad `Producto`:

1. **Anotaciones en el controlador**:
   - `@Controller`: Indica que esta clase es un controlador de Spring MVC.
   - `@RequestMapping("/admin")`: Especifica el prefijo de la URL para todas las solicitudes manejadas por este controlador.

2. **Inyección de dependencias**:
   - `@Autowired RepoCategoria repoCategoria;`: Inyecta automáticamente una instancia de `RepoCategoria` en este controlador. `RepoCategoria` es un repositorio de Spring Data JPA que se utiliza para interactuar con la base de datos para la entidad `Categoria`.
   - `@Autowired RepoProducto repoProducto;`: Inyecta automáticamente una instancia de `RepoProducto` en este controlador. `RepoProducto` es un repositorio de Spring Data JPA que se utiliza para interactuar con la base de datos para la entidad `Producto`.

3. **Método `findAll`**:
   - `@GetMapping("producto")`: Maneja las solicitudes GET en la URL `/admin/producto`.
   - `repoProducto.findAll()`: Recupera todos los productos de la base de datos y los agrega al modelo.
   - Retorna la vista `admin/productos`.

4. **Método `findByCategoria`**:
   - `@GetMapping("producto/categoria/{id}")`: Maneja las solicitudes GET en la URL `/admin/producto/categoria/{id}` donde `{id}` es el ID de la categoría.
   - `repoCategoria.findById(id)`: Busca la categoría por su ID.
   - `repoProducto.findByCategoria(padre)`: Recupera todos los productos asociados a la categoría y los agrega al modelo.
   - Retorna la vista `admin/productos-cat`.
   - Si la categoría no se encuentra, muestra una vista de error.

5. **Método `findByCategorias`**:
   - `@GetMapping("producto/categoria")`: Maneja las solicitudes GET en la URL `/admin/producto/categoria`.
   - `repoCategoria.findAll()`: Recupera todas las categorías de la base de datos y las agrega al modelo.
   - Retorna la vista `admin/productos-cat` con todos los productos y categorías.

6. **Métodos `addForm` y `postMethodName`**:
   - `addForm` maneja las solicitudes GET para mostrar el formulario de agregar un nuevo producto.
   - `postMethodName` maneja las solicitudes POST para procesar el formulario de agregar un nuevo producto.
   - `@ModelAttribute("producto") Producto producto`: Enlaza los datos enviados desde el formulario a un objeto `Producto` automáticamente.

7. **Métodos `deleteForm` y `delete`**:
   - `deleteForm` maneja las solicitudes GET para mostrar el formulario de confirmación de eliminación de un producto.
   - `delete` maneja las solicitudes POST para eliminar un producto por su ID.
   - Si el producto no se encuentra o si ocurre un error durante la eliminación, muestra una vista de error.

8. **Método `editForm`**:
   - `editForm` maneja las solicitudes GET para mostrar el formulario de edición de un producto.
   - Si el producto no se encuentra, muestra una vista de error.

Fíjate que no hay un método ni ruta para el POST del formulario editar. Resulta que si un producto existe en la base de datos se actualizará y si no existe, se crea uno nuevo. Esto lo sabemos si el **id** del producto está instanciado o si es **null** se trata de un producto nuevo.

### Servicio gestionar "mis pedidos"

Un cliente puede gestionar (ver) el listado de pedidos así como el detalle de los mismos. Deberemos implementar un listado y un maestro-detalle de pedidos.

RUTA            | METODO | ROL | Observaciones
----------------|--------|---------|--------------
/mis-pedidos | GET | cliente | Mostrar listado pedidos
/mis-pedidos/detalle/{id} | GET | cliente | Mostrar detalle del pedido con ese ID
/mis-pedidos/detalle | POST | cliente | Mostrar detalle del pedido con el ID que se pasa como parámetro

### Servicio envío (estados) pedidos

El operario gestiona los pedidos. Ve el listado de pedidos sin procesar y en el momento que cambia un pedido a "en proceso" se le asigna y será el encargado de enviarlo. Igualmente también debe tener un listado de pedidos "en preparación", "enviados" y "completados". Tendremos un maestro-detalle en función del estado y del operario. 

RUTA            | Verbo | Datos    | Observaciones
-----------------------|-----|---------|-------------
/pedidos        | GET    | nada     | Listado de pedidos en estado "REALIZADO"
/pedidos/{id}        | GET    | ID del pedido     | Formulario ¿desea servir este pedido?
/pedidos/{id}        | POST    | ID del pedido     | Se le asigna al operario que hizo login ese pedido
/pedidos/estado/{estado}   | GET    | estado     | Listado de pedidos en ese estado para el operario que ha hecho LOGIN
/pedidos/operario/{idOpe}   | GET    | ID operario y estado     | Listado de pedidos en ese estado para ese operario
/pedidos/operario/{idOpe}/estado/{estado}   | GET    | ID operario y estado     | Listado de pedidos en ese estado para ese operario


### Servicio carro de la compra

Un usuario con perfil cliente gestiona su carro de la compra:

RUTA            | METODO | Datos | Observaciones
------------|--------|---------|----------------------
/carro | GET | Nada | Ver la cesta de la compra
/carro/add/{id} | GET | ID de producto y cantidad | Formulario para añadir productos al carro
/carro/add/{id} | POST | ID de producto y cantidad | Añade realmente el producto al carro
/productos | GET | nada | Listado de productos de la tienda
/productos/{id} | GET | El ID del producto | Formulario para añadir el producto con ese ID a la cesta (post a /carro/add)
/carro/delete | GET | Nada | Formulario vaciar cesta de la compra (¿segurio?)
/carro/delete | POST | Nada | Vacía la cesta de la compra
/carro/delete/{id} | GET | ID de linea producto | Formulario eliminar de la cesta de la compra un producto (¿seguro?)
/carro/delete/{id} | POST | ID de linea producto | Eliminar de la cesta de la compra un producto
/carro/confirm | GET | nada | Formulario confirmar compra
/carro/confirm | POST | nada | Confirma compra


Antes de comenzar, al tratarse de un proceso de compra, que es individual e intransferible, vamos a necesitar un método en el controlador que nos diga el usuario que ha iniciado la sesión, para ello del contexto de la aplicación podemos obtener información de la autenticación:

```java
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
        Usuario cliente = repoUsuario.findByUsername(username).get(0);

        return cliente;
    }
```

Seguidamente vamos a explicar qué debe hacer cada endpoint así como la lógica asociada que lo resuelve. 

**Endpoint `/carro `**

Muestra el carro de la compra para el usuario que hizo login.

Un carro de la compra es un pedido en un estado especial que aún no tiene fecha, ni hora, ni otros datos.

Este controlador añade los datos del carro de la compra a la vista.

```java
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
        modelo.addAttribute("pedido", pedidos.size()>0 ? pedidos.get(0): new Pedido());        
        modelo.addAttribute("lineapedidos", lineaPedidos);
        modelo.addAttribute("total", total);

        // modelo.addAttribute("productos", productos );
        return "carro/carro";
    }
```

**End-point `/carro/edit/{id}`**

Para editar la cantidad de un calzado concreto que tenemos en el pedido tenemos este endpoint, que para un método GET muestra el formulario y para el método POST lo lleva a cabo. 

En este end-point el ID hace referencia a un objeto de tipo *LineaPedido*, es decir el detalle de un producto concreto para un pedido en cuestión.

**Por seguridad** hay que comprobar que ese objeto *LineaPedido* pertenece a un *Pedido* del usuario que hizo login. 

Para entenderlo vamos a explocar esta consulta para ver el detalle del pedido lineaPedido con ID=1 y el usuario con ID=2:

```sql
select username, pedido.id as "id_pedido", cantidad, producto.nombre
from usuario 
inner join pedido on pedido.cliente_id = usuario.id
inner join linea_pedido on linea_pedido.pedido_id = pedido.id
inner join producto on linea_pedido.producto_id = producto.id
where usuario.id=2 and linea_pedido.id=1;
```

Que daría como resultado algo similar a esto (sólo una línea):

username | id_pedido | cantidad | nombre
---------|-----------|----------|-------
cliente | 1 | 2 | Zapatillas Running Asics Gel-Nimbus


**Endpoint `/carro/confirm`**

En este método vemos dos métodos, GET o POST, en el GET nos mostrará un formulario de confirmación del pedido donde pondremos la dirección de envío y el teléfono de contacto para que el operador que prepare después el pedido pueda indicarlo al servicio de paquetería.

El método POST confirma el pedido, guarda los precios actuales en *LíneaPedido*, así como el total en *Pedido*. Igualmente le asocia al pedido la dirección de envío, el teléfono de contacto y pone el estado a *REALIZADO*.



\pagebreak

