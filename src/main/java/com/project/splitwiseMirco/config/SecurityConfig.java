package com.project.splitwiseMirco.config;

import com.project.splitwiseMirco.repository.UserRepository;
import com.project.splitwiseMirco.security.JwtAuthConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//public class SecurityConfig {
//
//    private final UserRepository userRepository;
//
//    public SecurityConfig(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/health", "/actuator/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .oauth2ResourceServer(oauth2 ->
//                        oauth2.jwt(jwt ->
//                                jwt.jwtAuthenticationConverter(
//                                        new JwtAuthConverter(userRepository)
//                                )
//                        )
//                );
//
//        return http.build();
//    }
//}

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        JwtAuthConverter jwtAuthConverter =
                new JwtAuthConverter(userRepository);

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthConverter)
                        )
                );

        return http.build();
    }
}
