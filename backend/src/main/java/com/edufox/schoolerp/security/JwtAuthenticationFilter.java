package com.edufox.schoolerp.security;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;import java.util.List;
@Component public class JwtAuthenticationFilter extends OncePerRequestFilter{
 private final JwtService jwt; public JwtAuthenticationFilter(JwtService jwt){this.jwt=jwt;}
 @Override protected void doFilterInternal(HttpServletRequest r,HttpServletResponse p,FilterChain c)throws ServletException,IOException{
  String h=r.getHeader("Authorization");if(h!=null&&h.startsWith("Bearer ")){try{Claims x=jwt.parse(h.substring(7));String role=x.get("role",String.class);var a=new UsernamePasswordAuthenticationToken(x.getSubject(),null,role==null?List.of():List.of(new SimpleGrantedAuthority("ROLE_"+role.toUpperCase())));a.setDetails(x);SecurityContextHolder.getContext().setAuthentication(a);}catch(RuntimeException e){SecurityContextHolder.clearContext();}}c.doFilter(r,p);
 }
}