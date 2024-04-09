# Spring Security

Por defecto Spring incorpora su propio sistema de login que está habilitado simplemente al añadir la dependencia en el pom.xml.

El usuario por defecto es **user** y la contraseña la puedes ver diferente y generada en cada arranque de la aplicación:

![Arranque Spring Boot](docs/springboot.png)

Así aparece el password:

![Password Spring Boot](docs/password.png)

### Spring Security

Las contraseñas deberán estar cifradas con BCRYPT, puedes usar este ejemplo para hacer pruebas:

Plaint text password | Hashed Password
---------------------|----------------
Secreto_123 | $2a$10$PMDCjYqXJxGsVlnve1t9Jug2DkDDckvUDl8.vF4Dc6yg0FMjovsXO


Para dar seguridad a la aplicación podemos crear una clase de configuración donde inyectamos los *beans* encargados de la seguridad. Además sería recomendable crear nuestros formularios de login y actualización de nuestros datos.

Ejemplo de Bean de configuración (puede ser código o un archivo XML):

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

      @Autowired 
      DataSource dataSource;
      
      @Autowired
      public void configure(AuthenticationManagerBuilder amb) throws Exception {
        amb.jdbcAuthentication()
            .dataSource(dataSource)
            .usersByUsernameQuery("select username, password, enabled "+
                "from usuario where username = ?")
            .authoritiesByUsernameQuery("select u.username, r.rol as 'authority' "+
                "from usuario u, rol_usuario r " +
                "where u.id=r.usuario_id and username = ?");
      }

      @Bean
      BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
      }

      @Bean
        public SecurityFilterChain filter(HttpSecurity http) throws Exception {
                
                // Con Spring Security 6.2 y 7: usando Lambda DSL

                return http
                        .authorizeHttpRequests((requests) -> requests
                                .requestMatchers("/webjars/**", "/img/**", "/js/**", "/register/**", "/ayuda/**", "/login", "/denegado")
                                .permitAll() 
                                .requestMatchers("/admin/**", "/admin/*/**" , "/admin/*/*/**")
                                //.authenticated()
                                .hasAuthority("GESTOR")
                                .requestMatchers("/pedidos/**", "/pedidos/*/**", "/pedidos/*/*/**", "/pedidos/*/*/*/**", "/pedidos/*/*/*/*/**")
                                //.authenticated()
                                .hasAuthority("OPERARIO")
                                .requestMatchers("/mis-pedidos/**", "/mis-pedidos/*/**", 
                                    "/productos/**", "/productos/*/**", 
                                    "/carro/**", "/carro/*/**")
                                //.authenticated()
                                .hasAuthority("CLIENTE")
                        //        .anyRequest().permitAll()
                        // ).headers(headers -> headers
                        //         .frameOptions(frameOptions -> frameOptions
                        //                 .sameOrigin())
                        // ).sessionManagement((session) -> session
                        //         .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        ).exceptionHandling((exception)-> exception.
                                accessDeniedPage("/denegado") )
                        .formLogin((formLogin) -> formLogin
                                //.loginPage("/login")
                                .permitAll()
                        ).rememberMe(
                                Customizer.withDefaults()
                        ).logout((logout) -> logout
                                .invalidateHttpSession(true)
                                .logoutSuccessUrl("/")
                                // .deleteCookies("JSESSIONID") // no es necesario, JSESSIONID se hace por defecto
                                .permitAll()                                
                        ).csrf((protection) -> protection
                                 .disable()
                        // ).cors((protection)-> protection
                        //          .disable()
                        ).build();

        }
}

```


\pagebreak

