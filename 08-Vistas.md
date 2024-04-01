# Vistas

Las vistas son componentes que se encargan de generar la representación visual de los datos para que puedan ser presentados al usuario. Las vistas son responsables de mostrar la información de una manera adecuada y estructurada, y permiten al usuario interactuar con la aplicación.

Thymeleaf es un motor de plantillas muy utilizado en aplicaciones Spring. Puedes acceder a su documentación en este enlace: <https://www.thymeleaf.org/doc/tutorials/3.1/thymeleafspring.html>.

Proporciona una forma sencilla y de integrar las plantillas HTML con el código Java en el lado del servidor gracias a:

1. Sintaxis amigable: Thymeleaf utiliza una sintaxis natural y fácil de leer que se asemeja a HTML. Esto facilita la creación y el mantenimiento de las plantillas, ya que no es necesario aprender una sintaxis nueva y compleja.
2. Expresiones: Thymeleaf permite utilizar expresiones en las plantillas para acceder a los datos y manipularlos. Estas expresiones son similares a las expresiones de lenguaje de plantillas (EL) utilizadas en otros motores de plantillas, lo que hace que sea fácil y familiar trabajar con ellas.
3. Integración con Spring: Thymeleaf está diseñado específicamente para trabajar con el framework de Spring. Se integra de manera transparente con otros componentes de Spring, como los controladores y los modelos, lo que simplifica el proceso de desarrollo de aplicaciones web.
4. Procesamiento del lado del servidor: Thymeleaf se ejecuta en el servidor, lo que significa que puede acceder a los datos y realizar operaciones antes de enviar la respuesta al cliente. Esto permite generar dinámicamente el contenido de las páginas en función de los datos y la lógica de negocio.
5. Thymeleaf ofrece una amplia gama de características adicionales, como la internacionalización, la validación de formularios, la manipulación de URL, la iteración de listas y la condicionalización de contenido. Estas características hacen que el desarrollo de aplicaciones web sea más eficiente y productivo.

En Spring, podemos situar nuestras plantillas (templates) en la carpeta **main/resources/templates**. Recuerda que el nombre del archivo debe ser lo mismo que retorna el controlador, quien es el encargado de buscar y/o procesar los datos para estas vistas.

Si queremos un ejemplo de CRUD completo, necesitaremos al menos tres vistas:

* Listar (de este listado, pulsando un botón podemos saltar a editar ese objeto o bien eliminarlo).
* Editar
* Crear

Para ayudarnos en esta tarea, como hay porciones del código que van a ser repetitivas, como las cabeceras, el pie de página, logotipos, menús..., usaremos fragmentos.

## Fragmentos

En Thymeleaf, los **fragments** son secciones de una plantilla HTML que se pueden reutilizar en varias páginas. Permiten separar y organizar el código HTML en componentes más pequeños y modulares, lo que facilita el mantenimiento y la reutilización del código.

Un fragmento se define en una plantilla mediante la etiqueta `<th:block>` y se puede utilizar en otras plantillas utilizando la directiva `th:insert` o `th:replace`. Aquí hay un ejemplo de cómo se puede definir y utilizar un fragmento en Thymeleaf:

1. Definición de un fragmento en una plantilla:

```html
<!-- plantilla fragmento.html -->
<th:block th:fragment="nombreDelFragmento">
    <!-- contenido del fragmento -->
    <p>Este es el contenido del fragmento</p>
</th:block>
```

2. Uso del fragmento en otra plantilla:

```html
<!-- otra plantilla.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Ejemplo de uso de fragmento</title>
</head>
<body>
    <div>
        <!-- inserta el fragmento -->
        <div th:insert="fragmento.html :: nombreDelFragmento"></div>
    </div>
</body>
</html>
```

En este ejemplo, el fragmento con el nombre "nombreDelFragmento" definido en el archivo `fragmento.html` se inserta en la plantilla `otra plantilla.html` utilizando la directiva `th:insert`. El contenido del fragmento se renderizará en el lugar donde se inserta.

Los fragments son especialmente útiles cuando se desea compartir código HTML común entre varias páginas, como encabezados, pies de página, menús de navegación, formularios, etc. Al utilizar fragments, se puede evitar la repetición de código y mantener una estructura modular y reutilizable en las plantillas Thymeleaf.


\pagebreak

