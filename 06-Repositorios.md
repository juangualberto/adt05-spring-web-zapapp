# Repositorios Spring

Los repositorios son una abstracción que se utiliza para acceder y manipular datos en una base de datos. Los repositorios facilitan la implementación del patrón de diseño de Repositorio en una aplicación.

En Spring, los repositorios se definen como interfaces que extienden de una de las interfaces proporcionadas por el módulo Spring Data, como JpaRepository, CrudRepository o PagingAndSortingRepository. Estas interfaces proporcionan métodos predefinidos para realizar operaciones comunes de persistencia, como guardar, eliminar, buscar y filtrar registros en la base de datos. Básicamente proporcionan una capa de abstracción para acceder a los datos de una base de datos de manera sencilla y eficiente, evitando la necesidad de escribir código repetitivo y consultas SQL complejas. Esto mejora la productividad del desarrollador y facilita el mantenimiento de la capa de persistencia en una aplicación Spring.

Los repositorios de Spring funcionan de la siguiente manera:

1. **Definición de la interfaz del repositorio**: Se crea una interfaz que extiende de una de las interfaces de repositorio proporcionadas por Spring Data. Esta interfaz define métodos para realizar operaciones CRUD y consultas personalizadas.
2. **Anotaciones y configuración**: Se utilizan anotaciones de Spring, como @Repository, para marcar la interfaz del repositorio y permitir que Spring la detecte y cree una implementación en tiempo de ejecución. También se configura la conexión a la base de datos y otras propiedades relacionadas en el archivo de configuración de Spring.
3. **Inyección de dependencias**: Los repositorios se inyectan en otras capas de la aplicación, como servicios o controladores, mediante la anotación @Autowired o mediante la inyección de dependencias de Spring.
4. **Uso de los métodos del repositorio**: En otras capas de la aplicación, se utilizan los métodos definidos en la interfaz del repositorio para realizar operaciones de persistencia. Estos métodos abstraen las consultas y operaciones CRUD, lo que simplifica la interacción con la base de datos y evita la necesidad de escribir consultas SQL complejas manualmente.
5. **Personalización y consultas personalizadas**: Los repositorios de Spring Data permiten personalizar las consultas mediante la definición de métodos con nombres específicos, utilizando convenciones de nomenclatura. También se pueden definir consultas personalizadas utilizando anotaciones como @Query, que permite escribir consultas en lenguajes como JPQL, SQL o MongoDB Query Language.

Para que los repositorios funcionen, hemos de explicar a Spring dónde y cómo almacenar la información. En la carpeta **main/resources** tenemos un archivo de configuración llamado `application.properties` que se utiliza en un proyecto de Spring para configurar propiedades relacionadas con la base de datos y otras configuraciones. Aunque también es posible en vez de tenerlo como archivo de propiedades, tenerlo como archivo en formato YAML, nosotros usaremos el formato nativo de propiedades Java:

```props
spring.datasource.url=jdbc:mysql://localhost:33306/gestion_inventario
spring.datasource.username=root
spring.datasource.password=zx76wbz7FG89k
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.session.store-type=jdbc
```

En este archivo estamos indicando:

- `spring.datasource.url`: Es la URL de conexión a la base de datos MySQL. En este caso, se está conectando a una base de datos llamada "gestion_inventario" en el localhost en el puerto 33306.

- `spring.datasource.username`: Es el nombre de usuario utilizado para autenticarse en la base de datos MySQL. En este caso, se utiliza el nombre de usuario "root".

- `spring.datasource.password`: Es la contraseña utilizada para autenticarse en la base de datos MySQL. En este caso, se utiliza la contraseña "zx76wbz7FG89k".

- `spring.jpa.properties.hibernate.dialect`: Especifica el dialecto de Hibernate a utilizar. En este caso, se utiliza el dialecto "org.hibernate.dialect.MySQL8Dialect" para trabajar con MySQL 8.

- `spring.jpa.hibernate.ddl-auto`: Especifica cómo Hibernate manejará la creación y actualización de la estructura de la base de datos. En este caso, se configura en "update", lo que significa que Hibernate actualizará automáticamente la estructura de la base de datos según las entidades definidas en el proyecto.

- `spring.session.store-type`: Especifica el tipo de almacenamiento de sesiones que se utilizará en la aplicación. En este caso, se configura en "jdbc" para almacenar las sesiones en la base de datos a través de JDBC.

Este archivo de configuración se utiliza fundamentalmente para indicar la configuración de la base de datos, la configuración de Hibernate y otras configuraciones relacionadas con el proyecto de Spring. Estas propiedades se cargan automáticamente durante la ejecución del proyecto y se utilizan para establecer la conexión con la base de datos, configurar Hibernate y otros componentes del proyecto.

A continuación vamos a ver cómo definir los repositorios. Esto es sólo la definición, para ver cómo usarlos puedes echar un ojo al siguiente apartado: **Controladores**.

## RepoCategoria

Cada categoría viene identificada por un código numérico único, un nombre y una descripción. Además cada categoría tiene una (y sólo una) categoría padre. Para preguntar a una categoría por sus hijos usaremos su repositorio.

```java
package com.iesvdc.acceso.zapateria.zapapp.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iesvdc.acceso.zapateria.zapapp.modelos.Categoria;

@Repository
public interface RepoCategoria extends JpaRepository<Categoria, Long> {
    @Query("SELECT c FROM Categoria c WHERE c.padre = :padre")
    List<Categoria> findByPadre(Categoria padre);
}

```

Observa la consulta `@Query("SELECT c FROM Categoria c WHERE c.padre = :padre")`, en esta consulta JPQL:

- `SELECT c`: Indica que queremos seleccionar entidades `Categoria`.
- `FROM Categoria c`: Especifica la entidad de la que queremos seleccionar, que es `Categoria` y la abreviamos como `c`.
- `WHERE c.padre = :padre`: Filtra las categorías basadas en su atributo `padre`, que debe ser igual al objeto `Categoria` proporcionado como parámetro `padre`.

Con esta consulta JPQL personalizada, obtenemos todas las categorías que tienen el mismo padre que el objeto `Categoria` proporcionado.

No obstante este código es redundante pues Spring tiene la capacidad de generar por nosotros cualquier **"findBy\*"**, en verdad basta con hacer: 

```java
package com.iesvdc.acceso.zapateria.zapapp.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iesvdc.acceso.zapateria.zapapp.modelos.Categoria;

@Repository
public interface RepoCategoria extends JpaRepository<Categoria, Long> {
    List<Categoria> findByPadre(Categoria padre);
}

```

## RepoCodigoPostal

Los códigos postales que tenemos en la base de datos son accesibles mediante este repositorio:

```java
@Repository
public interface RepoCodigoPostal extends JpaRepository <CodigoPostal, Long> {
    
}
```

## RepoDireccion

```java
@Repository
public interface RepoDireccion extends JpaRepository<Direccion, Long> {

}
```

## RepoLineaPedido

```java
@Repository
public interface RepoLineaPedido extends JpaRepository<LineaPedido, Long> {

}
```

## RepoPedido

```java
@Repository
public interface RepoProducto extends JpaRepository<Producto, Long> {

}
```

## RepoProducto

```java
@Repository
public interface RepoProducto extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoria(Categoria padre);
}

```

## RepoRolUsuario

```java
@Repository
public interface RepoRolUsuario extends JpaRepository<RolUsuario, Long> {

}
```

## RepoTelefono

```java
@Repository
public interface RepoTelefono extends JpaRepository<Telefono, Long> {

}
```

## RepoUsuario

```java
@Repository
public interface RepoUsuario extends JpaRepository<Usuario, Long> {

}

```


\pagebreak

