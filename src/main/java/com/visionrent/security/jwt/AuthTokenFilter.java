package com.visionrent.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    public static final Logger AuthTokenFilterLogger= LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    public UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwtToken=parseJWT(request);
        //validate our token in every request
        try {
            if(jwtToken!=null && jwtUtils.validateJwtToken(jwtToken)){
                //get email from this token
                String email=jwtUtils.getEmailFromToken(jwtToken);

                //KEY PART THAT CALLS USER DETAILS FROM SECURITY SERVICE LAYER
                UserDetails userDetails=userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authenticationToken=
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                //we are sending all userDetails information to the securityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }
        }catch (Exception e){
          AuthTokenFilterLogger.error("User not found{}:", e.getMessage());
        }
        filterChain.doFilter(request,response);
    }

    private String parseJWT(HttpServletRequest request){
        String header=request.getHeader("Authorization");
        if(StringUtils.hasText(header) && header.startsWith("Bearer ")){
            return header.substring(7);
        }
        return null;
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        AntPathMatcher antPathMatcher=new AntPathMatcher();
        return antPathMatcher.match("/register",request.getServletPath())
                || antPathMatcher.match("/login",request.getServletPath());
}}
