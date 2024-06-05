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

## Ejemplo de index.html por autoridades

Veamos con el **landing page** o página de bienvenida cómo podemos gestionar las vistas en función de la persona que hizo login. En nuestro archivo **index.html** tenemos lo siguiente:

```html
<!DOCTYPE html>
<html lang="es">

<head>
  <title>Su Zapatería Online: INCIO</title>
  <th:block th:replace="~{fragmentos/general.html :: headerfiles}"></th:block>
</head>

<body>  
  <div th:replace="~{fragmentos/general.html :: navigation}"> </div>

  <!-- Section for anonymous users -->
  <div sec:authorize="hasAuthority('ROLE_ANONYMOUS')" class="container">
    <div class="card" style="width: 18rem;">
      <img src="./img/img.jpeg" class="card-img-top" alt="...">
      <div class="card-body">
        <h5 class="card-title">Bienvenido a Zapato Veloz</h5>
        <p class="card-text">Su tienda de zapatos de confianza.</p>
        <a href="/login" class="btn btn-primary">Login</a>
        <a href="/register" class="btn btn-primary">Registrarse</a>
      </div>
    </div>
  </div>

  <!-- Section for clients -->
  <div sec:authorize="hasAuthority('CLIENTE')" class="container">
    <h3>Bienvenido <span th:text="${#authentication.name}"></span> a Zapato Veloz </h3>
    <p> Pulse para comenzar a 
      <a href="/productos" class="btn btn-info">COMPRAR!</a>
    </p>
  </div>

  <!-- Section for operarios -->
  <div sec:authorize="hasAuthority('OPERARIO')" class="container">
    <h3>El Operario <span th:text="${#authentication.name}"></span> entró a Zapato Veloz </h3>
    <p> Pulse para comenzar a 
      <a href="/pedidos" class="btn btn-info"> 
        <i class="fa-solid fa-bag-shopping"></i> Atender pedidos
      </a>
    </p>
  </div>

  <!-- Section for gestores -->
  <div sec:authorize="hasAuthority('GESTOR')" class="container">
    <h3>Detectado acceso como administrador</h3>
    <p>El usuario <span th:text="${#authentication.name}"></span> entró a Zapato Veloz </p>
  </div>

  <div th:replace="~{fragmentos/general.html :: footer}"></div>
</div>
</body>

</html>
```

Esta página Thymeleaf se adapta al rol del usuario autenticado, mostrando contenido diferente para usuarios anónimos, clientes, operarios y gestores. Usa fragmentos de Thymeleaf para modularizar el contenido común (cabecera, navegación, pie de página) y utiliza la integración con Spring Security para mostrar contenido específico basado en las autoridades del usuario.


### Encabezado y Archivos de Cabecera

```html
<!DOCTYPE html>
<html lang="es">

<head>
  <title>Su Zapatería Online: INCIO</title>
  <th:block th:replace="~{fragmentos/general.html :: headerfiles}"></th:block>
</head>
```
- **DOCTYPE y html**: Define el tipo de documento y el idioma como español.
- **title**: Establece el título de la página.
- **th:block th:replace="~{fragmentos/general.html :: headerfiles}"**: Thymeleaf incluye un bloque que reemplaza con los archivos de cabecera definidos en el fragmento `headerfiles` dentro de `general.html`.

### Navegación

```html
<body>  
  <div th:replace="~{fragmentos/general.html :: navigation}"> </div>
```
- **th:replace="~{fragmentos/general.html :: navigation}"**: Incluye el fragmento `navigation` desde `general.html`, que probablemente contiene el menú de navegación de la página.

### Sección para Usuarios Anónimos

```html
<div sec:authorize="hasAuthority('ROLE_ANONYMOUS')" class="container">
  <div class="card" style="width: 18rem;">
    <img src="./img/img.jpeg" class="card-img-top" alt="...">
    <div class="card-body">
      <h5 class="card-title">Bienvenido a Zapato Veloz</h5>
      <p class="card-text">Su tienda de zapatos de confianza.</p>
      <a href="/login" class="btn btn-primary">Login</a>
      <a href="/register" class="btn btn-primary">Registrarse</a>
    </div>
  </div>
</div>
```
- **sec:authorize="hasAuthority('ROLE_ANONYMOUS')"**: Esta sección solo se muestra si el usuario tiene la autoridad `ROLE_ANONYMOUS`, es decir, no está autenticado.
- **Tarjeta de Bienvenida**: Muestra una tarjeta con un mensaje de bienvenida, una imagen, y botones para `Login` y `Registro`.

### Sección para Clientes

```html
<div sec:authorize="hasAuthority('CLIENTE')" class="container">
  <h3>Bienvenido <span th:text="${#authentication.name}"></span> a Zapato Veloz </h3>
  <p> Pulse para comenzar a 
    <a href="/productos" class="btn btn-info">COMPRAR!</a>
  </p>
</div>
```
- **sec:authorize="hasAuthority('CLIENTE')"**: Esta sección se muestra solo si el usuario tiene la autoridad `CLIENTE`.
- **Bienvenida Personalizada**: Muestra un mensaje de bienvenida con el nombre del usuario autenticado y un botón para ir a la página de productos.

### Sección para Operarios

```html
<div sec:authorize="hasAuthority('OPERARIO')" class="container">
  <h3>El Operario <span th:text="${#authentication.name}"></span> entró a Zapato Veloz </h3>
  <p> Pulse para comenzar a 
    <a href="/pedidos" class="btn btn-info"> 
      <i class="fa-solid fa-bag-shopping"></i> Atender pedidos
    </a>
  </p>
</div>
```
- **sec:authorize="hasAuthority('OPERARIO')"**: Esta sección se muestra solo si el usuario tiene la autoridad `OPERARIO`.
- **Mensaje y Enlace**: Muestra un mensaje de bienvenida con el nombre del operario y un botón para atender pedidos.

### Sección para Gestores

```html
<div sec:authorize="hasAuthority('GESTOR')" class="container">
  <h3>Detectado acceso como administrador</h3>
  <p>El usuario <span th:text="${#authentication.name}"></span> entró a Zapato Veloz </p>
</div>
```
- **sec:authorize="hasAuthority('GESTOR')"**: Esta sección se muestra solo si el usuario tiene la autoridad `GESTOR`.
- **Mensaje de Administrador**: Muestra un mensaje indicando que el usuario ha entrado como administrador y muestra su nombre.

### Pie de Página

```html
<div th:replace="~{fragmentos/general.html :: footer}"></div>
```
- **th:replace="~{fragmentos/general.html :: footer}"**: Incluye el fragmento `footer` desde `general.html`, que contiene el pie de página del sitio web.

## Ejemplo de plantilla general para nuestro sitio Web

Nuestra Web debe mantener constantes las diferentes partes de la página, como son los menús, el pié de página, los estilos o los colores. Para ello lo ideal es crear una o varias plantillas (más fácil de mantener) que usen todas las páginas (como por ejemplo la anterior). 

En el anterior ejemplo de **index.html** vemos que en la carpeta **fragmentos** tenemos un fichero llamado **general.html** al que referenciamos distintas partes. Veamos cómo funciona: 

```html
<!DOCTYPE html>
<html lang="es">

<head th:fragment="headerfiles">
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css" />
    <link rel="stylesheet" href="/webjars/font-awesome/6.5.2/css/all.min.css" />
</head>

<body>
    <div class="container-fluid">
        <div th:fragment="navigation">
            <nav class="navbar navbar-expand-lg bg-primary" data-bs-theme="dark">
                <div class="container-fluid">
                    <a class="navbar-brand" href="/">Zapato veloz</a>
                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                        aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarNav">
                        <ul class="navbar-nav me-auto">
                            <li class="nav-item">
                                <a class="nav-link" href="/">Inicio </a>
                            </li>
                            <li sec:authorize="hasAuthority('GESTOR')" class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                                    aria-expanded="false">
                                    Usuarios
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a id="menu_modulo" class="dropdown-item" href="/admin/usuarios">Listado</a>
                                    </li>
                                    <li><a id="menu_enseñanza" class="dropdown-item" href="/admin/usuarios/add">Alta</a>
                                    </li>
                                </ul>
                            </li>
                            <li sec:authorize="hasAuthority('GESTOR')" class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                                    aria-expanded="false">
                                    Categorías
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a id="menu_modulo" class="dropdown-item" href="/admin/categoria">Listado</a>
                                    </li>
                                    <li><a id="menu_enseñanza" class="dropdown-item"
                                            href="/admin/categoria/add">Alta</a></li>
                                </ul>
                            </li>
                            <li sec:authorize="hasAuthority('GESTOR')" class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                                    aria-expanded="false">
                                    Productos y precios
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a id="menu_modulo" class="dropdown-item" href="/admin/producto">Listado</a>
                                    </li>
                                    <li><a id="menu_modulo" class="dropdown-item" href="/admin/producto/categoria">Por
                                            categorías</a></li>
                                    <li><a id="menu_enseñanza" class="dropdown-item" href="/admin/producto/add">Alta</a>
                                    </li>
                                </ul>
                            </li>

                            
                            <li sec:authorize="hasAnyAuthority('CLIENTE', 'OPERARIO')" class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                                    aria-expanded="false">
                                    <i class="fa-regular fa-user"></i> | Mis datos
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a id="menu_modulo" class="dropdown-item" href="/mis-datos"> <i
                                                class="fa-solid fa-user"></i> Personal </a></li>
                                    <li><a id="menu_enseñanza" class="dropdown-item" href="/mis-datos/telefonos"> <i
                                                class="fa-solid fa-phone"></i> Teléfonos </a></li>
                                    <li><a id="menu_enseñanza" class="dropdown-item" href="/mis-datos/direcciones"> <i
                                                class="fa-solid fa-address-book"></i> Direcciones </a></li>
                                </ul>
                            </li>
                            <li sec:authorize="hasAuthority('CLIENTE')" class="nav-item">
                                <a class="nav-link" href="/productos"> <i class="fa-solid fa-cart-plus"></i> | Comprar
                                </a>
                            </li>
                            <li sec:authorize="hasAuthority('CLIENTE')" class="nav-item">
                                <a class="nav-link" href="/carro"> <i class="fa-solid fa-cart-shopping"></i> | Carrito
                                </a>
                            </li>
                            <li sec:authorize="hasAuthority('CLIENTE')" class="nav-item">
                                <a class="nav-link" href="/mis-pedidos"> <i class="fa-solid fa-list"></i> | Mis pedidos
                                </a>
                            </li>
                            

                            <li sec:authorize="hasAuthority('OPERARIO')" class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                                    aria-expanded="false">
                                    Gestión Pedidos
                                </a>
                                <ul class="dropdown-menu">
                                    <li>
                                        <a id="menu_modulo" class="dropdown-item" href="/pedidos">
                                            Para preparar </a>
                                    </li>
                                    <li>
                                        <a id="menu_enseñanza" class="dropdown-item" href="/pedidos/en-preparacion">
                                            En Preparación </a>
                                    </li>
                                    <li>
                                        <a id="menu_enseñanza" class="dropdown-item" href="/pedidos/enviados">
                                            Enviados </a>
                                    </li>
                                    <li>
                                        <a id="menu_enseñanza" class="dropdown-item" href="/pedidos/mis-pedidos">
                                            Todos mis pedidos </a>
                                    </li>
                                </ul>
                            </li>

                            <li sec:authorize="hasAuthority('OPERARIO')" class="nav-item">
                                <a class="nav-link" href="/pedidos"> Pedidos para servir </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="/ayuda">Ayuda </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="/acerca">Sobre esta Web </a>
                            </li>
                            <li sec:authorize="hasAnyAuthority('CLIENTE', 'OPERARIO', 'GESTOR')" class="nav-item">
                                <a class="nav-link" href="/logout">Desconexión </a>
                            </li>
                        </ul>

                    </div>
                </div>
            </nav>
            
        </div>

        <div th:fragment="header">
            <h2 class="text-center">Departamento de Informática </h2>

            <div class="d-flex justify-content-center">
                <div class="card bg-light text-center" style="width: 18rem;">
                    <img class="card-img-top" src="/img/ESCUDO_CHICO.png" alt="Escudo Informática">
                    <div class="card-body">
                        <h5 class="card-title">Zapatería Online</h5>
                        <p class="card-text"> Gestión integral de zapatería On-Line. </p>
                        <a href="#" id="menu_informe" class="btn btn-primary">Generar informe</a>
                    </div>
                </div>


            </div>
        </div>



        <div th:fragment="footer">
            <footer>
                <div class="text-center">
                    <hr />

                    <p>Entró en el sistema como:
                        <span th:text="${#authentication.name}"></span>
                        (<span th:text="${#authentication.authorities[0]}"></span>)
                    <p sec:authorize="hasAuthority('GESTOR')">
                        <b>Entró en el sistema como admin</b>
                    </p>
                    </p>
                    <p>
                        <span class="text-center">2024. IES Virgen del Carmen. CFGS Desarrollo Multiplataforma.</span>
                    </p>
                    <hr />
                </div>
                <script src="/webjars/jquery/jquery.min.js"></script>
                <script src="/webjars/popper.js/umd/popper.min.js"></script>
                <script src="/webjars/bootstrap/js/bootstrap.js"></script>
                <script src="/js/aux.js"></script>
            </footer>
        </div>
    </div>
</body>

</html>
```

Este template de Thymeleaf utiliza fragmentos para estructurar la página HTML de manera modular, facilitando la reutilización y el mantenimiento del código. La integración con Spring Security permite mostrar contenido específico según las autoridades del usuario autenticado, proporcionando una experiencia de usuario personalizada. La combinación de Bootstrap y FontAwesome garantiza un diseño visual atractivo y funcional:



```html
<!DOCTYPE html>
<html lang="es">

<head th:fragment="headerfiles">
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css" />
    <link rel="stylesheet" href="/webjars/font-awesome/6.5.2/css/all.min.css" />
</head>
```

### Explicación del `<head>`

- **DOCTYPE y html**: Define el tipo de documento y el idioma como español.
- **th:fragment="headerfiles"**: Define un fragmento llamado `headerfiles`. Este fragmento incluye la meta información y los enlaces a los estilos CSS, que se pueden reutilizar en otras partes de la aplicación.
- **meta tags**: Configura el carácter, compatibilidad y las dimensiones de la vista.
- **link tags**: Incluyen Bootstrap y FontAwesome desde WebJars para estilos y iconos.

```html
<body>
    <div class="container-fluid">
        <div th:fragment="navigation">
            <nav class="navbar navbar-expand-lg bg-primary" data-bs-theme="dark">
                <div class="container-fluid">
                    <a class="navbar-brand" href="/">Zapato veloz</a>
                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                        aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarNav">
                        <ul class="navbar-nav me-auto">
                            <li class="nav-item">
                                <a class="nav-link" href="/">Inicio </a>
                            </li>
                            <!-- Menú solo visible para gestores -->
                            <li sec:authorize="hasAuthority('GESTOR')" class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                                    aria-expanded="false">
                                    Usuarios
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a id="menu_modulo" class="dropdown-item" href="/admin/usuarios">Listado</a>
                                    </li>
                                    <li><a id="menu_enseñanza" class="dropdown-item" href="/admin/usuarios/add">Alta</a>
                                    </li>
                                </ul>
                            </li>
                            <!-- Otros menús -->
                            <li sec:authorize="hasAuthority('GESTOR')" class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                                    aria-expanded="false">
                                    Categorías
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a id="menu_modulo" class="dropdown-item" href="/admin/categoria">Listado</a>
                                    </li>
                                    <li><a id="menu_enseñanza" class="dropdown-item"
                                            href="/admin/categoria/add">Alta</a></li>
                                </ul>
                            </li>
                            <!-- Más menús según la autoridad del usuario -->
                            <!-- ... -->
                            <li sec:authorize="hasAuthority('OPERARIO')" class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                                    aria-expanded="false">
                                    Gestión Pedidos
                                </a>
                                <ul class="dropdown-menu">
                                    <li>
                                        <a id="menu_modulo" class="dropdown-item" href="/pedidos">
                                            Para preparar </a>
                                    </li>
                                    <li>
                                        <a id="menu_enseñanza" class="dropdown-item" href="/pedidos/en-preparacion">
                                            En Preparación </a>
                                    </li>
                                    <li>
                                        <a id="menu_enseñanza" class="dropdown-item" href="/pedidos/enviados">
                                            Enviados </a>
                                    </li>
                                    <li>
                                        <a id="menu_enseñanza" class="dropdown-item" href="/pedidos/mis-pedidos">
                                            Todos mis pedidos </a>
                                    </li>
                                </ul>
                            </li>
                            <!-- Otros enlaces de navegación -->
                            <!-- ... -->
                            <li sec:authorize="hasAnyAuthority('CLIENTE', 'OPERARIO', 'GESTOR')" class="nav-item">
                                <a class="nav-link" href="/logout">Desconexión </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>
        </div>

        <div th:fragment="header">
            <h2 class="text-center">Departamento de Informática </h2>
            <div class="d-flex justify-content-center">
                <div class="card bg-light text-center" style="width: 18rem;">
                    <img class="card-img-top" src="/img/ESCUDO_CHICO.png" alt="Escudo Informática">
                    <div class="card-body">
                        <h5 class="card-title">Zapatería Online</h5>
                        <p class="card-text"> Gestión integral de zapatería On-Line. </p>
                        <a href="#" id="menu_informe" class="btn btn-primary">Generar informe</a>
                    </div>
                </div>
            </div>
        </div>

        <div th:fragment="footer">
            <footer>
                <div class="text-center">
                    <hr />
                    <p>Entró en el sistema como:
                        <span th:text="${#authentication.name}"></span>
                        (<span th:text="${#authentication.authorities[0]}"></span>)
                    <p sec:authorize="hasAuthority('GESTOR')">
                        <b>Entró en el sistema como admin</b>
                    </p>
                    </p>
                    <p>
                        <span class="text-center">2024. IES Virgen del Carmen. CFGS Desarrollo Multiplataforma.</span>
                    </p>
                    <hr />
                </div>
                <script src="/webjars/jquery/jquery.min.js"></script>
                <script src="/webjars/popper.js/umd/popper.min.js"></script>
                <script src="/webjars/bootstrap/js/bootstrap.js"></script>
                <script src="/js/aux.js"></script>
            </footer>
        </div>
    </div>
</body>

</html>
```

### Explicación del `<body>`

#### Navegación
- **th:fragment="navigation"**: Define un fragmento de navegación reutilizable.
- **nav**: Barra de navegación Bootstrap que incluye enlaces y menús desplegables.
- **sec:authorize="hasAuthority('GESTOR')"**: Los elementos del menú solo se muestran si el usuario tiene la autoridad `GESTOR`.
- **sec:authorize="hasAnyAuthority('CLIENTE', 'OPERARIO')"**: Los elementos del menú solo se muestran si el usuario tiene cualquiera de las autoridades `CLIENTE` o `OPERARIO`.
- **sec:authorize="hasAnyAuthority('CLIENTE', 'OPERARIO', 'GESTOR')"**: Enlace de desconexión que se muestra si el usuario tiene cualquiera de las autoridades mencionadas.

#### Encabezado
- **th:fragment="header"**: Define un fragmento de encabezado reutilizable.
- **h2**: Encabezado centrado.
- **Card**: Tarjeta Bootstrap que incluye una imagen y un texto de bienvenida.

#### Pie de Página
- **th:fragment="footer"**: Define un fragmento de pie de página reutilizable.
- **footer**: Pie de página con información del usuario autenticado y su autoridad.
- **sec:authorize="hasAuthority('GESTOR')"**: Mensaje adicional para usuarios con autoridad `GESTOR`.
- **Scripts**: Incluye scripts de jQuery, Popper.js y Bootstrap desde WebJars y un script personalizado `aux.js`.



## Ejemplo de fragmento para formularios

El proceso de poder replicar código en diferentes partes nos simplifica la tarea de mantener y/o actualizar el código además de entenderlo mejor.

En Spring, tanto para actualizar un objeto en la base de datos, como para darlo de alta, usamos el mismo método en los repositorios, el método `save`. Por tanto podremos reutilizar tanto para editar como para crear nuevos productos, la misma porción de la página. 

Igualmente podemos aplicar este concepto a partes reutilizables como el detalle un un pedido. Tanto los operarios a la hora de preparar un pedido, como los usuarios a la hora de ver sus pedidos, necesitan la misma porción de código, concretamente esta **fichero fragmentos/pedido.html**:

```html

<div th:fragment="detalle-pedido">

    <h3>Detalle de pedidos realizados en esta Web</h3>

<div th:if="${pedido != null}" class="m-2 border rounded border-2">
    <table id="listado" class="table table-striped">
        <thead>
            <tr>
                <th>#</th>
                <td th:text="${pedido.id}"> </td>
            </tr>
        </thead>
        <tbody>
            <tr>
                <th>Nombre y apellidos</th>
                <td>
                    <span th:text="${pedido.cliente.nombre}"></span>
                    <span th:text="${pedido.cliente.apellido}"></span>
                </td>
            </tr>
            <tr>
                <th>Fecha</th>
                <td th:text="${pedido.fecha}"> </td>
            </tr>
            <tr>
                <th>Estado</th>
                <td th:text="${pedido.estado}"> </td>
            </tr>
            <tr>
                <th>Dirección envío</th>
                <td
                    th:text="${pedido.direccion} ? |${pedido.direccion.tipoVia} ${pedido.direccion.nombreVia},  ${pedido.direccion.numero} | : 'Sin dirección' ">
                </td>
            </tr>
            <tr>
                <th>Teléfono</th>
                <td th:text="|+(${pedido.telefono.codigoPais}) ${pedido.telefono.numero}|"></td>
            </tr>
        </tbody>
    </table>
</div>

<div th:if="${pedido.lineaPedidos != null}" class="m-2 border rounded border-2">
    <table id="listado" class="table table-striped">
        <thead>
            <tr>
                <th>Producto</th>
                <!-- <th>descripcion</th> -->
                <th>categoría</th>
                <th>Talla</th>
                <th>Precio</th>
                <th>Cantidad</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="lineaPedido : ${pedido.lineaPedidos}">
                <td th:text="${lineaPedido.producto.nombre}"></td>
                <!-- <td th:text="${lineaPedido.producto.descripcion}"></td> -->
                <td
                    th:text="${lineaPedido.producto.categoria} != null ? ${lineaPedido.producto.categoria.nombre} : 'Sin categoria'">
                </td>
                <td th:text="${lineaPedido.producto.talla}"></td>
                <td th:text="|${lineaPedido.precio} EUROS|"></td>
                <td th:text="${lineaPedido.cantidad}"></td>
            </tr>
            <tr>
                <th colspan="4">TOTAL</th>
                <td th:text="|${pedido.total} EUROS|"></td>
            </tr>
        </tbody>
    </table>
</div>
</div>

```

Ejemplo de detalle de un pedido para los operarios **pedidos/servir.html**:

```html
<!DOCTYPE html>
<html lang="es">

<head>
  <title>Su Zapatería Online: Gestión de Pedidos</title>
  <th:block th:replace="~{fragmentos/general.html :: headerfiles}"></th:block>
</head>

<body>  
  <div th:replace="~{fragmentos/general.html :: navigation}"> </div>

    <div th:replace="~{fragmentos/pedido.html :: detalle-pedido}"></div>

    <div class="container">
        <form method="post">
            <button type="submit" class="btn btn-primary">
                <i class="fa-solid fa-bag-shopping"></i> Preparar
            </button>
            <a class="btn btn-danger" href="/pedidos">
                <i class="fa-solid fa-backward"></i> Voler atrás
            </a>
        </form>
    </div>
  <div th:replace="~{fragmentos/general.html :: footer}"></div>
</div>
</body>

</html>
```

\pagebreak

