package ro.shoponline.security;
import org.springframework.context.annotation.*; import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  return http.authorizeHttpRequests(a->a.requestMatchers("/","/products/**","/register","/css/**","/images/**").permitAll()
    .requestMatchers("/admin/**").hasRole("ADMIN").anyRequest().authenticated())
   .formLogin(f->f.loginPage("/login").defaultSuccessUrl("/",true).permitAll())
   .logout(l->l.logoutSuccessUrl("/")).build();
 }
}
