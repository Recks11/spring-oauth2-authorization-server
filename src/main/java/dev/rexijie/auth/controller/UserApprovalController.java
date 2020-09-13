package dev.rexijie.auth.controller;

import dev.rexijie.auth.errors.DumbRequestException;
import dev.rexijie.auth.model.Client;
import dev.rexijie.auth.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Set;

/**
 * @author Rex Ijiekhuamen
 * 09 Sep 2020
 */
@Controller
@SessionAttributes("authorizationRequest")
@Slf4j
public class UserApprovalController {
    private final ClientService clientService;

    public UserApprovalController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/oauth/confirm_access")
    public String confirmAccessPage(Model model) {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.getAttribute("authorizationRequest");
        if (authorizationRequest == null)
            return "redirect:/oauth/login";

        String clientId = authorizationRequest.getClientId();
        if (clientId == null) throw new DumbRequestException("No client");
        Set<String> scope = authorizationRequest.getScope();
        var client = (Client) clientService.loadClientByClientId(clientId);

        model.addAttribute("client_name", client.getClientName());
        model.addAttribute("scopes", scope);

        return "confirmaccess";
    }
}
