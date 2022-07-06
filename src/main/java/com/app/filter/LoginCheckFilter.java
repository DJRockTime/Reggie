package com.app.filter;


import com.alibaba.fastjson.JSON;
import com.app.common.BaseContext;
import com.app.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@Slf4j
//@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    // 路径匹配器， 支持通配符
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        /*
         * 1. 获取本次请求的URL
         * 2. 判断本次请求是否需要处理
         * 3. 如果不需要处理，则直接放行
         * 4. 判断登录状态，如果已登录，则直接放行
         * 5. 如果未登录则返回未登录结果
         * */

        // 1.
        String requestURI = request.getRequestURI();

        // 定义不需要处理的请求路径
        String [] urls = new String[] {
                "/app/employee/login",
                "/app/employee/logout",
                "/app/common/upload",
                "/app/css/**",
                "/app/images/**",
                "/app/user/sendMsg",
                "/app/user/login",
                //"/app/employee"
                //"/backend/**", // 静态资源
                //"/front/**", // 静态资源
        };

        // 2. 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        // 3.
        if(check) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4-1 判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，用户id为： {}", request.getSession().getAttribute("employee") );

            // 当前线程获取的id
            long empId = Thread.currentThread().getId();
            //log.info("Thread id: {}", id);
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }

        // 4-2 判断登录状态，则直接放行
        if(request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，用户id为： {}", request.getSession().getAttribute("user") );

            // 当前线程获取的id
            long userId = Thread.currentThread().getId();
            //log.info("Thread id: {}", id);
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }

        // 5. 通过输出流的方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("Not Login")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param requestURI 请求地址
     * @return bool
     */
    public boolean check(String[] urls, String  requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match) {
                return true;
            }
        }
        return false;
    }
}
