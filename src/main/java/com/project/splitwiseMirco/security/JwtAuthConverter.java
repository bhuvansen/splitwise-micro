package com.project.splitwiseMirco.security;

import com.project.splitwiseMirco.entity.User;
import com.project.splitwiseMirco.repository.UserRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;

public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository userRepository;

    public JwtAuthConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        String userId = jwt.getSubject();
        if (email != null) {
            userRepository.findByEmail(email).orElseGet(() -> {
                User user = new User();
                user.setId(userId);
                user.setEmail(email);
                user.setName(name);
                user.setCreatedAt(Instant.now());
                return userRepository.save(user);
            });
        }

        return new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
