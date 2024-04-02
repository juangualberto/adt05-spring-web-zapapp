# Las clases entidad

En Spring Java llamamos **POJO** o Plain Old Java Objects a aquellas clases Java sencillas que no necesitan heredar o implementar interfaces del framework Spring. En este caso también lo haremos para denominar a las clases entidad de nuestra aplicación.

Para facilitar el trabajo usaremos anotaciones de Lombok y JPA.

**Anotaciones Lombok**:

* `@Data`: Genera todos los getters y setters, toString, hashCode y compare. Genera el constructor con todos los atributos.
* `@NoArgsConstructor`: Genera el constructor vacío que hace falta para JAXB, por ejemplo.


**Anotaciones jakarta.persistence** (antiguamente javax.persistence):

* `@Entity`: El objeto (clase) marcado con esta anotación será una tabla en la base de datos.
* `@Id`: El atributo marcado con esta anotación será la clave primaria de la tabla correspondiente al objeto o clase.
* `@ManytoOne`: Marcamos así atributos que son a su vez entidades (clases modelo) con los que tenemos una relación de muchos a uno. Lo ponemos en atributos que son a su vez clases entidad que yo he definido (para indicar clave foránea)
* `@ManyToAny`: Igual que anterior pero cuando puede ser NULL la clave foránea referenciada
* `@ManyToMany`: Muchos a muchos, se creará una entidad intermedia como cuando en un modelo entidad-relación tenemos una relación de muchos a muchos.
* `@OnetoMany`: Marcamos así atributos que son a su vez entidades (clases modelo) con los que tenemos una relación de uno a muchos (será una lista o similar este atributo).
* `@OneToOne`: Relación de uno-a-uno.
* `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Unido a `@Id`, será el *AUTO_INCREMENT* de MySQL (en la tabla asiciada a ese objeto).
* `@Column(length = 25)`: En un atributo de una clase entidad fija la longitud del VARCHAR, por ejemplo.

Veamos la siguiente clase usuario:

```java
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String username;
    String password;
    String tipo;
    String email;
}
```

1. La anotación **@Entity** indica que esta clase es una entidad persistente y se mapeará a una tabla en la base de datos.
2. La anotación **@Data** es una anotación de Lombok que genera automáticamente los métodos equals(), hashCode(), toString(), getters y setters para todos los campos de la clase.
3. La anotación **@NoArgsConstructor** es otra anotación de Lombok que genera un constructor sin argumentos para la clase.
4. La anotación **@Id** indica que el campo id es el identificador único de la entidad.
5. La anotación **@GeneratedValue** especifica la estrategia de generación de valores para el campo id. En este caso, se utiliza la estrategia **GenerationType**.**IDENTITY**, que indica que el valor del campo se generará automáticamente por la base de datos.
6. Los campos username, password, tipo y email representan las propiedades de un usuario.

Esta clase Usuario es una entidad persistente que representa a un usuario en el sistema. Contiene campos como username, password, tipo y email, donde tipo es una relación con la entidad Rol (en este caso un *Enum*). Esta clase se mapeará a una tabla en la base de datos y se utilizará para almacenar y recuperar información de usuarios.

## Los POJOs del proyecto

A continuación vamos a ver cómo hemos generado cada una de las clases modelo del proyecto de la zapatería a partir del diagrama de clases del apartado anterior.

### Categoría

```java
@Entity
@Data
@NoArgsConstructor
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;
    @ManyToOne
    private Categoria padre;
}
```

### Código Postal

```java
@Entity
@Data
@NoArgsConstructor
public class CodigoPostal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer CP;
    private String localidad;
    private String municipio;
    private String comunidad;
    private String pais;    
}

```

### Dirección

```java
@Entity
@Data
@NoArgsConstructor
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tipoVia;
    private String nombreVia;
    private String número;
    private String planta;
    private String puerta;
    private String portal;
    private String nombre;
    @ManyToOne    
    private CodigoPostal codpos;
    @ManyToOne
    private Usuario usuario;
}
```

### Estado

```java
public enum Estado {
    CARRITO, REALIZADO, PREPARANDO, ENVIADO, COMPLETADO, INCIDENCIA
}

```

### Línea Pedido

```java
@Entity
@Data
@NoArgsConstructor
public class LineaPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Float precio;
    private Integer cantidad;
    @ManyToOne
    private Producto producto;
    @ManyToOne
    private Pedido pedido;
}

```

### Pedido 

```java
@Entity
@Data
@NoArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha;
    private String observaciones;
    private Float descuento;
    @Enumerated(EnumType.STRING)
    private Estado estado;
    @OneToMany(mappedBy = "pedido")
    private List<LineaPedido> lineaPedidos;
    @ManyToOne
    private Usuario asignadoOperario;
    @ManyToOne
    private Direccion direccion;
}
```

### Producto

```java
@Entity
@Data
@NoArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripción;
    private String talla;
    private Float precio;
    @ManyToOne
    private Categoria categoria;
}

```

### Rol

```java
public enum Rol {
    OPERARIO, CLIENTE, GESTOR
}
```

### Rol Usuario

```java
@Entity
@Data
@NoArgsConstructor
public class RolUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    @ManyToOne
    private Usuario usuario;
}
```

### Teléfono

```java
@Entity
@Data
@NoArgsConstructor
public class Telefono {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long codigoPais;
    private Long numero;
    private String nombre;
    @ManyToOne
    private Usuario usuario;
}

```

### Usuario

```java
@Entity
@Data
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String username;
    private String password;
    
    @OneToMany(mappedBy = "usuario")
    private List<Direccion> direcciones;
    @OneToMany(mappedBy = "usuario")
    private List<Telefono> telefonos;
    @OneToMany(mappedBy = "usuario")
    private List<RolUsuario> roles;

}

```


\pagebreak

