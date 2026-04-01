package com.example.Machine_Management_System_Backend.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String secret_key = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    public String generateToken(UserDetails userDetails, String role) {


        return Jwts.builder().
                setSubject(userDetails.getUsername()).
                claim("role", role).
                setIssuedAt(new Date()).
                setExpiration(new Date(System.currentTimeMillis()+86400000)).
                signWith(SignatureAlgorithm.HS256,secret_key).
                compact();

    }


    public Claims parseToken(String token) {
        return Jwts.parser().
                setSigningKey(secret_key).
                parseClaimsJws(token).
                getBody();
    }


    public String extractUsername(String token) {
        return parseToken(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return parseToken(token).getSubject().equals(userDetails.getUsername())
                && !parseToken(token).getExpiration().before(new Date());
    }

}
