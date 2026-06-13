
package com.jobportal.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component  // Tells Spring to manage this class
public class JwtUtil {

    // Secret key to sign our tokens
    // This should be kept SECRET — never share this!
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token expires in 24 hours (in milliseconds)
    private final long EXPIRATION_TIME = 86400000;

    // Generate token for a user
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)        // Store email inside token
                .setIssuedAt(new Date())  // When token was created
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)      // Sign with our secret key
                .compact();
    }

    // Get email from token
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Check if token is valid
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
