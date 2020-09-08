package com.benoly.auth.controller;

import com.benoly.auth.model.Client;
import com.benoly.auth.service.ClientService;
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

import java.util.List;
import java.util.Map;

/**
 * @author Rex Ijiekhuamen
 */
@Controller
public class AuthorizationController {

    private final ObjectMapper objectMapper;
    private final ClientService clientService;

    public AuthorizationController(ObjectMapper objectMapper,
                                   ClientService clientService) {
        this.objectMapper = objectMapper;
        this.clientService = clientService;
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

    @GetMapping("/oauth/confirm_access")
    public String confirmAccessPage(Model model, @RequestParam Map<String, String> params) {
        String clientId = (String) getParam(params, "client_id");
        var client = (Client) clientService.loadClientByClientId(clientId);
        var scopes = extractScopesFromParams(params);

        model.addAttribute("client_name", client.getName());
        model.addAttribute("scopes", scopes);

        return "confirmaccess";
    }

    private Object getParam(Map<String, String> params, String name) {
        var value = params.get(name);
        if (value == null) return "";

        return value;
    }

    private List<String> extractScopesFromParams(Map<String, String> params) {
        String scopes = (String) getParam(params, "scope");
        String[] scopeArray = new String[]{scopes};
        if (scopes.contains(","))
            scopeArray = scopes.split(",");

        if (scopes.contains(" "))
            scopeArray = scopes.split(" ");

        return List.of(scopeArray);
    }
}
