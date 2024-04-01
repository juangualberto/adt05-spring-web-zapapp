# Las clases entidad

En Spring Java llamamos POJO o Plain Old Java Objects a aquellas clases Java sencillas que no necesitan heredar o implementar interfaces del framework Spring. En este caso también lo haremos para denominar a las clases entidad de nuestra aplicación.

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

\pagebreak

