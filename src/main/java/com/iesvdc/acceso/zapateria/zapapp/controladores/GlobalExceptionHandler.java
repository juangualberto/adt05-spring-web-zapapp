package com.iesvdc.acceso.zapateria.zapapp.controladores;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.iesvdc.acceso.zapateria.zapapp.excepciones.CarroException;

/**
 * Con este controlador, podemos gestionar excepciones "a medida", por ejemplo al hacer transacciones
 * y lanzar el rollback si llegamos a un punto que necesitamos lanzar la excepción, como cuando
 * tenemos en el carrito de la compra más productos de los que hay en stock.
 */

@ControllerAdvice // Esto hace que el controlador maneje excepciones globales
public class GlobalExceptionHandler {

    @ExceptionHandler(CarroException.class) // Manejar cualquier excepción
    public ModelAndView handleException(CarroException ex) {
        ModelAndView modelAndView = new ModelAndView("error"); // Vista de error
        modelAndView.addObject("titulo", "Error al procesar su carrito");
        modelAndView.addObject("mensaje", ex.getMessage()); // Puedes usar getMessage() para obtener el mensaje de la excepción
        return modelAndView;
    }
    
}
