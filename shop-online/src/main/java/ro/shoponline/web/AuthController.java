package ro.shoponline.web;
import ro.shoponline.model.User; import ro.shoponline.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*;
@Controller
public class AuthController {
 private final UserRepository users; private final PasswordEncoder encoder;
 public AuthController(UserRepository u,PasswordEncoder e){users=u;encoder=e;}
 @GetMapping("/login") String login(){return "login";}
 @GetMapping("/register") String register(Model m){m.addAttribute("user",new User());return "register";}
 @PostMapping("/register") String save(@ModelAttribute User user,Model m){
  if(users.findByEmail(user.getEmail()).isPresent()){m.addAttribute("error","Adresa de email este deja folosita.");return "register";}
  user.setPassword(encoder.encode(user.getPassword()));user.setRole("CLIENT");users.save(user);return "redirect:/login?registered";
 }
}
