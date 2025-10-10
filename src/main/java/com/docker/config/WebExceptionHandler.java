// /src/main/java/com/docker/config/GlobalExceptionHandler.java

package com.docker.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute; 
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest; 

@ControllerAdvice
public class WebExceptionHandler {

    
    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNotFound(HttpServletRequest req, Exception ex) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("error/404");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(HttpServletRequest req, Exception ex) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("error/500");
        return mav;
    }
}