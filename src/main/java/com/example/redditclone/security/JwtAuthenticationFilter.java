package com.example.redditclone.security;

import com.example.redditclone.exceptions.SpringRedditCloneException;
import com.example.redditclone.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationToken = request.getHeader("Authorization");

        try {
            if(authorizationToken != null && authorizationToken.startsWith("Bearer ")) {
                String jwtToken = authorizationToken.substring(7);
                String username = jwtUtil.getUsernameFromToken(jwtToken);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if(jwtUtil.validateToken(jwtToken, userDetails)) {
                    log.info("Jwt Token is valid");
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new SpringRedditCloneException("Jwt Token is not valid");
                }

            } else {
                log.error("Jwt does not start with 'Bearer '");
            }
        } catch (Exception e) {
            throw new SpringRedditCloneException("Jwt Token is not valid.");
        }

        filterChain.doFilter(request, response);
    }
}
