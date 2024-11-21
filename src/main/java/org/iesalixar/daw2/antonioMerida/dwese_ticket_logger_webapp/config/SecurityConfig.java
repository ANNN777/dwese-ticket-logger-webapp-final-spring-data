package org.iesalixar.daw2.antonioMerida.dwese_ticket_logger_webapp.config;

import org.iesalixar.daw2.antonioMerida.dwese_ticket_logger_webapp.handlers.CustomOAuth2FailureHandler;
import org.iesalixar.daw2.antonioMerida.dwese_ticket_logger_webapp.handlers.CustomOAuth2SuccessHandler;
import org.iesalixar.daw2.antonioMerida.dwese_ticket_logger_webapp.services.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Activa la seguridad basada en métodos
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Autowired
    private CustomOAuth2FailureHandler customOAuth2FailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Entrando en el método securityFilterChain");

        http
                .authorizeHttpRequests(auth -> {
                    logger.debug("Configurando autorización de solicitudes HTTP");
                    auth
                            .requestMatchers("/", "/hello").permitAll() // Acceso anónimo
                            .requestMatchers("/admin").hasRole("ADMIN") // Solo ADMIN
                            .requestMatchers(
                                    "/regions",
                                    "/provinces",
                                    "/supermarkets",
                                    "/locations",
                                    "/categories"
                            ).hasRole("MANAGER") // Solo MANAGER
                            .requestMatchers("/tickets").hasRole("USER") // Solo USER
                            .anyRequest().authenticated(); // Cualquier otra solicitud requiere autenticación
                })
                .formLogin(form -> {
                    logger.debug("Configurando formulario de inicio de sesión");
                    form
                            .loginPage("/login") //  página de inicio de sesión personalizada
                            .defaultSuccessUrl("/", true) // Redirige al inicio después del login exitoso
                            .permitAll();
                })
                .sessionManagement(session -> {
                    logger.debug("Configurando política de gestión de sesiones");
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED); // Usa sesiones cuando sea necesario
                })
                .oauth2Login(oauth2 -> {
                    logger.debug("Configurando login con OAuth2");
                    oauth2
                            .loginPage("/login")        // Reutiliza la página de inicio de sesión personalizada
                            .successHandler(customOAuth2SuccessHandler) // Usa el Success Handler personalizado
                            .failureHandler(customOAuth2FailureHandler); // Handler para fallo en autenticación
                });

        logger.info("Saliendo del método securityFilterChain");
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Entrando en el método passwordEncoder");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        logger.info("Saliendo del método passwordEncoder");
        return encoder;
    }
}