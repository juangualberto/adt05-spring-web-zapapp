# Controladores

Los controladores son componentes que se utilizan para manejar las solicitudes HTTP y generar las respuestas correspondientes. Actúan como intermediarios entre el cliente y el servidor, procesando las solicitudes entrantes y produciendo las respuestas adecuadas.

En el contexto de Spring MVC (Model-View-Controller), los controladores reciben las solicitudes HTTP, extraen los datos necesarios (consultan bases de datos, colecciones de documentos...), invocan la lógica de negocio apropiada y devuelven una respuesta al cliente. Se definen como clases anotadas con la anotación @Controller o @RestController, que les permite ser reconocidos y administrados por el contenedor de Spring.

Funcionalidades clave de los controladores en Spring:

1. **Gestión de rutas**: Los controladores definen métodos que se asocian a rutas o URLs específicas. Esto se logra mediante la anotación **@RequestMapping** en Spring MVC o mediante anotaciones más específicas como **@GetMapping**, **@PostMapping**, etc.
2. **Recepción de parámetros**: Los métodos de los controladores pueden recibir parámetros enviados en la solicitud HTTP, como parámetros de consulta, encabezados, datos de formulario o cuerpo de la solicitud. Estos parámetros se pueden vincular directamente a los parámetros del método del controlador utilizando anotaciones como **@RequestParam**, **@PathVariable**, **@RequestHeader**, etc.
3. **Lógica de negocio**: Los controladores son responsables de invocar la lógica de negocio adecuada para procesar la solicitud. Esto puede implicar la interacción con servicios, repositorios u otros componentes de la aplicación para realizar operaciones, procesar datos y preparar la respuesta.
4. **Generación de peticiones**: Los controladores devuelven objetos que representan la respuesta a enviar al cliente. Estos objetos pueden ser cadenas de texto, objetos **JSON**, **vistas** (templates) a renderizar, archivos, redirecciones, entre otros. La selección del tipo de respuesta se basa en la anotación del método del controlador y la configuración de Spring.
5. **Manejo de excepciones**: Los controladores también pueden manejar excepciones que se produzcan durante el procesamiento de la solicitud. Esto permite capturar errores, realizar acciones específicas (como devolver un código de estado HTTP adecuado o mostrar una página de error personalizada) y mantener un flujo controlado de la aplicación.

## Ejemplo de controlador sencillo

\pagebreak

