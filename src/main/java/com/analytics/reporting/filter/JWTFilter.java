package com.analytics.reporting.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final RedisTemplate<String,Object> redisTemplate;

    @Value("${jwt.secret}")
    private  String secret;

    private final Key key= Keys.hmacShaKeyFor(secret.getBytes());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if(token!=null && token.startsWith("Bearer "))
        {
            try{
                String jwtToken = token.substring(7);
                Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key)
                        .build().parseClaimsJws(jwtToken);

                String appId= claims.getBody().get("appId").toString();
                String tokenType =claims.getBody().get("type").toString();
                //write token logic for comparing version from redis
                Integer currTokenVersion =(Integer) claims.getBody().get("token_version");
                Integer originalTokenVersion = (Integer)redisTemplate.opsForValue().get("app_token_version"+appId);
                String status = redisTemplate.opsForValue().get("app_status"+appId).toString();

                if(tokenType.equalsIgnoreCase("refresh_token") || currTokenVersion!=originalTokenVersion || status.equalsIgnoreCase("disable"))
                {
                    response.setStatus(401);
                    return;
                }
                request.setAttribute("appId",appId);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(claims.getBody().getSubject(),null,new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            } catch (Exception e) {
                response.setStatus(401);
                return;
            }
            filterChain.doFilter(request,response);
        }
    }
}
