package dev.rexijie.auth.config.interceptors;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This is interceptor invalidates sessions after the Authorization code flow is complete
 * To ensure the Authorization server is stateless
 */
@Slf4j
public class SessionInvalidatingHandlerInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null && modelAndView.getView() instanceof RedirectView) {
            RedirectView redirectView = (RedirectView) modelAndView.getView();
            String redirectUrl = redirectView.getUrl();
            if (redirectUrl == null) return;
            if (redirectUrl.contains("code=") || redirectUrl.contains("error=")) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    log.debug("invalidating session {}", session.getId());
                    session.invalidate();
                }
            }
        }
    }
}
