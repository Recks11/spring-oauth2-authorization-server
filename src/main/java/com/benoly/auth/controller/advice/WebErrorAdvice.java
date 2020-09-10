package com.benoly.auth.controller.advice;

import com.benoly.auth.errors.MalformedRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Rex Ijiekhuamen
 * 09 Sep 2020
 */
@ControllerAdvice
@Slf4j
public class WebErrorAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(IllegalStateException ex,
                                                 HttpServletRequest request) {
        if (request != null) {
            log.error("request {} lead to an error", request.getRequestURI());
        }
        log.error(ex.getMessage(), ex);
        ModelAndView mv = new ModelAndView();
        mv.addObject("httpstatus", 400);
        mv.setViewName("error/400");
        return mv;
    }

    @ExceptionHandler(MalformedRequestException.class)
    public ModelAndView handleMalformedRequest(MalformedRequestException ex, HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView("error/400", HttpStatus.BAD_REQUEST);
        modelAndView.addObject("httpstatus", 400);

        return modelAndView;
    }
}
