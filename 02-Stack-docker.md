# Contenedores Docker para acelerar el desarrollo

Docker es una plataforma de contenedores que permite empaquetar y distribuir aplicaciones junto con sus dependencias en contenedores ligeros y portátiles. Estos contenedores son unidades de software autónomas que encapsulan todo lo necesario para ejecutar una aplicación, incluyendo el código, las bibliotecas, las herramientas y las configuraciones. Docker proporciona una forma fácil y eficiente de crear, distribuir y ejecutar aplicaciones en diferentes entornos.

Recuerda que debemos poner cada servicio en un contenedor diferente en Docker se basa en los principios de modularidad, escalabilidad y aislamiento de aplicaciones, además de facilitar su gestión:

* **Modularidad**: Al colocar cada servicio en un contenedor separado, se puede seguir el principio de diseño de software de "separación de intereses". Cada contenedor contiene un componente o servicio específico de la aplicación, lo que facilita la gestión y el mantenimiento del sistema en general. Además, al utilizar contenedores independientes, es posible actualizar o cambiar un servicio sin afectar a otros componentes de la aplicación, lo que brinda flexibilidad y facilita la evolución de la aplicación con el tiempo.
* **Escalabilidad**: Al dividir la aplicación en servicios individuales en contenedores separados, es posible escalar vertical u horizontalmente los recursos de manera independiente según las necesidades de cada servicio. Esto permite asignar más recursos a los servicios que requieren mayor capacidad y optimizar el rendimiento general de la aplicación.
* **Aislamiento**: Docker proporciona aislamiento entre los contenedores, lo que significa que cada contenedor tiene su propio entorno aislado. Esto garantiza que los servicios no interfieran entre sí, evitando posibles conflictos o dependencias entre ellos. Además, si un contenedor falla, los demás continúan funcionando sin problemas, lo que mejora la tolerancia a fallos y la disponibilidad del sistema.
* **Facilidad de gestión**: Al tener servicios individuales en contenedores separados, la gestión de cada servicio se simplifica. Cada contenedor se puede configurar, monitorear y escalar de forma independiente. Además, es más sencillo realizar pruebas y depurar problemas en un servicio específico sin afectar al resto de la aplicación.

## Mysql y Adminer

A partir de documentación de la imagen oficial <https://hub.docker.com/_/mysql>, creamos un fichero docker-compose para poder levantar estos dos servicios:

* **MySQL**: El servicio de la archiconocida base de datos relacional (a día de hoy la segunda en el ránking <https://db-engines.com/en/ranking>).  Por defecto el servidor está en el puerto 3306, luego nuestros programas deberán conectarse a ese puerto para *hablar* con la base de datos.
* **Adminer**: Un servicio Web escrito en PHP para conectarse y gestionar bases de datos relacionales. Aunque realmente no es necesario, nos resultará muy útil a la hora de interactuar con la base de datos vía Web sin necesidad de usar software adicional o instalar MySQL Workbench. Por defecto está en el puerto 8080, luego si abrimos nuestro navegador y escribimos http://HOST:8080 donde HOST es el nombre del equipo donde está instalado, podemos acceder vía Web a este entorno que a su vez se conecta a la base de datos (recuerda que MySQL estaba en el puerto 3306).

Como es posible que tengamos alguno de esos puertos estándar ya en uso, nosotros vamos a *natear* a otros puertos en el *docker-compose*. ¿Qué es eso de *natear*? Pues hacer NAT, recuerda que los contenedores están inicialmente aislados dentro de tu equipo, en una red virtual interna, luego para exponerlos a nuestra red local LAN, podemos "*conectarlos*" a un puerto de nuestra máquina *física*. Así por ejemplo vamos a exponer el puerto 3306 del MySQL en el puerto 33306 de nuestra máquina real. Lo mismo con el puerto 8080 del Adminer en el puerto 8181 de la máquina real.

Vamos a crear también una red virtual llamada *skynet* para que todos los contenedores *hablen* entre sí.

```yaml
version: '3.1'
services:
  db:
    image: mysql
    command: --default-authentication-plugin=mysql_native_password
    restart: "no"
    environment:
      MYSQL_ROOT_PASSWORD: zx76wbz7FG89k
    networks:
      - skynet
    ports:
      - 33306:3306

  adminer:
    image: adminer
    restart: "no"
    networks:
      - skynet
    ports:
      - 8181:8080
networks:
  skynet:

```



\pagebreak

