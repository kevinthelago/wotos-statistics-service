package com.wotos.wotosstatisticsservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

/**
 * Parses and validates HS256 JWTs issued by the WoToS user service. Tokens
 * are expected to carry a subject (user id) and a {@code roles} claim holding
 * the user's role names (e.g. {@code ["ADMIN"]}); each role is mapped to a
 * {@code ROLE_<name>} Spring Security authority.
 */
@Service
public class JwtService {

    private final SecretKey key;

    public JwtService(@Value("${env.jwt_secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }

    public Collection<? extends GrantedAuthority> extractAuthorities(Claims claims) {
        Object raw = claims.get("roles");
        if (raw instanceof List<?> roles) {
            return roles.stream()
                    .map(String::valueOf)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .map(GrantedAuthority.class::cast)
                    .toList();
        }
        return List.of();
    }

}
