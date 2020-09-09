package com.benoly.auth.controller;

import com.benoly.auth.errors.DumbRequestException;
import com.benoly.auth.model.Client;
import com.benoly.auth.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

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
        if (authorizationRequest == null) throw new DumbRequestException("how'd you even get here?");
        String clientId = authorizationRequest.getClientId();
        Set<String> scope = authorizationRequest.getScope();
        var client = (Client) clientService.loadClientByClientId(clientId);

        model.addAttribute("client_name", client.getName());
        model.addAttribute("scopes", scope);

        return "confirmaccess";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(IllegalStateException ex, ServletWebRequest request) {

        log.error(ex.getMessage(), ex);
        return new ModelAndView("error");
    }
}
