package com.lenceria.sistema_stock.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    
    private static final String SECRET_KEY = "LenceriaFlavioSecretKeySuperSeguraParaElSistemaDeStock2026";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    
    // El token dura 10 horas
    private static final long EXPIRE_DURATION = 10 * 60 * 60 * 1000;

    public String generarToken(String username, String rol) {
        return Jwts.builder()
                .setSubject(username)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("ERROR: El Token expiró.");
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.out.println("ERROR: La firma del token es inválida.");
        } catch (Exception e) {
            System.out.println("ERROR DESCONOCIDO: " + e.getMessage());
        }
        return false;
    }

    public String extraerUsuario(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}
