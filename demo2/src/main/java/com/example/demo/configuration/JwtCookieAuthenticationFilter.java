package com.example.demo.configuration;

import com.example.demo.service.BlacklistService;
import com.example.demo.service.UserDetailServiceCustomizer;
import com.example.demo.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {
    private final BlacklistService blacklistService;
    private final JwtDecoder jwtDecoder;
    private final CookieUtil cookieUtil;
    private final UserDetailServiceCustomizer userDetailServiceCustomizer;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if(path.equals("/api/auth/refresh") || path.equals("/login") || path.equals("/create")){
            filterChain.doFilter(request,response);
            return;
        }

        String accessToken = cookieUtil.extractCookieValue(request,"access_token");
        if(accessToken == null){
            filterChain.doFilter(request,response);
            return;
        }
        try{
            Jwt jwt = jwtDecoder.decode(accessToken);
            UserDetails userDetails = userDetailServiceCustomizer.loadUserByUsername(jwt.getSubject());
            UsernamePasswordAuthenticationToken auth
                = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("error:" + e.getMessage());
        }
        filterChain.doFilter(request,response);
    }


}
