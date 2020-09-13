package dev.rexijie.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * @author Rex Ijiekhuamen
 */
@Controller
@SessionAttributes("authorizationRequest")
public class AuthorizationController {

    private final ObjectMapper objectMapper;

    public AuthorizationController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping("/api/introspect")
    @ResponseBody
    public ResponseEntity<String> home(@AuthenticationPrincipal Authentication authentication) throws Exception {
        var as = objectMapper.writeValueAsString(authentication);
        return new ResponseEntity<>(as, HttpStatus.OK);
    }

    @GetMapping("/oauth/login")
    public String loginPage(Model model, @RequestParam(required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "BAD CREDENTIALS");
        }
        return "login";
    }

    @GetMapping("/oauth/logout")
    public String logout() {
        return "logout";
    }

}
