package com.app.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebCorsFilter implements Filter {
    //private static final long MAX_AGE = 24 * 60 * 60;
    private static final String MAX_AGE = "86400";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        //response.setHeader("Access-Control-Allow-Origin", "/*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, HEAD, PUT");
        //response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Max-Age", MAX_AGE);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "access-control-allow-origin, authority, content-type, version-info, X-Requested-With");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
