package com.example.demo.configuration;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class JwtTypeFilter extends OncePerRequestFilter {

    private static final String REFRESH_PATH = "/api/auth/refresh";  // endpoint refresh của bạn

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Nếu là endpoint refresh → pass luôn (không check header)
        if (REFRESH_PATH.equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Không có header Bearer → pass (có thể là public hoặc cần filter khác)
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            Date expiration = claimsSet.getExpirationTime();
            if (expiration != null && expiration.before(new Date())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Token expired\"}");
                return;
            }

            String tokenType = (String) claimsSet.getClaim("type");  // chữ thường "type"

            if ("refresh".equals(tokenType)) {
                // Refresh token không được dùng Bearer ở endpoint khác /refresh
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Refresh token not allowed for this endpoint\"}");
                return;
            }

            // Nếu là access token → tiếp tục
            filterChain.doFilter(request, response);

        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid token format\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Token validation failed\"}");
        }
    }
}
