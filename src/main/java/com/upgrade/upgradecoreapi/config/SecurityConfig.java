package com.upgrade.upgradecoreapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Quitamos esta barrera para que Angular pueda enviar datos luego
                .authorizeHttpRequests(auth -> auth
                        // ¡AQUÍ ESTÁ LA MAGIA! Le decimos al guardia que deje pasar a cualquiera a ver Swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // Pero que para todo lo demás, exija identificación
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
