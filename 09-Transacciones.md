# Transacciones

## Introdución

En el contexto de Java y Spring, el concepto de transacciones se refiere a un conjunto de operaciones que se ejecutan como una unidad indivisible de trabajo. Es decir, todas las operaciones dentro de una transacción deben completarse correctamente, de lo contrario, ninguna de ellas se aplicará. Este enfoque garantiza la consistencia y la integridad de los datos en la base de datos.

En términos de Spring, la gestión de transacciones se realiza principalmente a través de anotaciones y configuraciones que Spring ofrece. Spring proporciona una abstracción sobre las API de transacciones subyacentes, lo que facilita la gestión de transacciones sin tener que preocuparse por los detalles específicos de la implementación de las transacciones.

### Conceptos Clave

1. **Atomicidad**: Todas las operaciones dentro de la transacción deben completarse con éxito. Si alguna operación falla, se debe revertir toda la transacción.
2. **Consistencia**: La base de datos debe pasar de un estado consistente a otro estado consistente.
3. **Aislamiento**: Las operaciones dentro de una transacción deben ser invisibles para otras transacciones hasta que se completen.
4. **Durabilidad**: Una vez que una transacción se ha completado, sus cambios deben ser permanentes incluso si ocurre una falla del sistema.

### Ejemplo en Spring con Spring Data JPA

Para demostrar cómo funciona una transacción en Spring, consideremos un ejemplo simple con una aplicación de Spring Boot que usa Spring Data JPA.

#### Paso 1: Configuración de la Aplicación

Primero, configura tu proyecto para usar Spring Boot, Spring Data JPA y una base de datos H2 en memoria.

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

#### Paso 2: Definir Entidades y Repositorios

Define una entidad simple `User` y su repositorio.

```java
// User.java
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    // getters and setters
}
```

```java
// UserRepository.java
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
```

#### Paso 3: Servicio con Gestión de Transacciones

Ahora, crea un servicio donde aplicarás la gestión de transacciones. Usa la anotación `@Transactional` para gestionar la transacción.

```java
// UserService.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createUser(String name) {
        User user = new User();
        user.setName(name);
        userRepository.save(user);

        if ("error".equals(name)) {
            throw new RuntimeException("Simulated error");
        }
    }
}
```

#### Paso 4: Controlador para Probar el Servicio

Finalmente, crea un controlador para probar el servicio.

```java
// UserController.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public String createUser(@RequestParam String name) {
        try {
            userService.createUser(name);
            return "User created successfully";
        } catch (RuntimeException e) {
            return "User creation failed: " + e.getMessage();
        }
    }
}
```

### Explicación

1. **Anotación @Transactional**: En el método `createUser` del `UserService`, la anotación `@Transactional` indica que el método debe ejecutarse dentro de una transacción. Si alguna excepción no verificada (RuntimeException) se lanza, la transacción se revertirá automáticamente.
   
2. **Simulación de Error**: Si el nombre del usuario es "error", se lanza una `RuntimeException` para simular un error. En este caso, debido a la anotación `@Transactional`, la transacción se revertirá, y el usuario no será guardado en la base de datos.

### Prueba

Para probar esto, inicia la aplicación y usa una herramienta como `curl` o Postman para enviar una solicitud POST.

- Para crear un usuario con éxito:
  ```sh
  curl -X POST "http://localhost:8080/users?name=John"
  ```

- Para provocar un error y revertir la transacción:
  ```sh
  curl -X POST "http://localhost:8080/users?name=error"
  ```

En el primer caso, el usuario "John" se guarda en la base de datos. En el segundo caso, debido a la excepción lanzada, la transacción se revierte y el usuario "error" no se guarda.

Este es un ejemplo básico de cómo se pueden gestionar las transacciones en una aplicación de Spring utilizando Spring Data JPA.

Veamos a continuación un caso real, como puede ser el carro de la compra, que, si no puede realizarse la compra completa por falta de stock, no permite completar la compra.

## Ejemplo del carro de la compra

Al confirmar el carro de la compra, si ha pasado cierto tiempo, 

```java
@Controller
public class ControCarrito {

    [...]
 
    @PostMapping("/carro/confirmar")
    @Transactional(rollbackOn = Exception.class)
    public String confirm(
        @ModelAttribute("lineaPedido") @NonNull Pedido pedido,
        Model modelo) throws Exception {

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
                        throw new Exception(
                            "No queda suficiente stock de: " + 
                            p.getNombre() + 
                            " para completar el pedido. Sólo quedan: " + 
                            p.getStock()+" unidades y en el pedido se solicitan: " + 
                            lp.getCantidad()+". Intente poner menos unidades para completar el pedido.");
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

    [...]

}
```

\pagebreak
