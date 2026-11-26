package mz.co.aprendaacodar.security;
import jakarta.servlet.*;import jakarta.servlet.http.*;import mz.co.aprendaacodar.user.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;import org.springframework.security.core.authority.SimpleGrantedAuthority;import org.springframework.security.core.context.SecurityContextHolder;import org.springframework.stereotype.Component;import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;import java.util.List;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
 private final JwtService jwt;private final UserRepository users;
 public JwtAuthenticationFilter(JwtService jwt,UserRepository users){this.jwt=jwt;this.users=users;}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String h=req.getHeader("Authorization");if(h!=null&&h.startsWith("Bearer "))try{var user=users.findById(java.util.UUID.fromString(jwt.subject(h.substring(7)))).orElse(null);if(user!=null&&user.getStatus()==mz.co.aprendaacodar.common.enums.Enums.UserStatus.ACTIVE){var auth=new UsernamePasswordAuthenticationToken(user.getId(),null,List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole())));SecurityContextHolder.getContext().setAuthentication(auth);}}catch(Exception ignored){SecurityContextHolder.clearContext();}
  chain.doFilter(req,res);
 }
}
