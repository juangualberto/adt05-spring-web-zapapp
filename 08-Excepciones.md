# Manejo de excepciones

Spring ofrece soporte para gestionar excepciones programáticamente, de manera que podemos capturar cualquier excepción sin que nuestra aplicación tenga que ser detenida.

Para ampliar tienes un buen tutorial en la Web de Baeldung: [Error Handling for REST with Spring](https://www.baeldung.com/exception-handling-for-rest-with-spring).

### Controlador de excepciones global

Con este controlador, podemos gestionar excepciones "a medida", por ejemplo al hacer transacciones y lanzar el rollback si llegamos a un punto que necesitamos lanzar la excepción, como cuando tenemos en el carrito de la compra más productos de los que hay en stock.

```java
@ControllerAdvice // Esto hace que el controlador maneje excepciones globales
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class) // Manejar cualquier excepción
    public ModelAndView handleException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("error"); // Vista de error
        modelAndView.addObject("titulo", "Error en la aplicación");
        modelAndView.addObject("mensaje", ex.getMessage()); // Puedes usar getMessage() para obtener el mensaje de la excepción
        return modelAndView;
    }
}
```

Este gestor de excepciones hará que se muestre la vista "error" con el mensaje que le digamos.

\pagebreak
