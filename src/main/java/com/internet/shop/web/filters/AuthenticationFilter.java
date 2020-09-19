package com.internet.shop.web.filters;

import com.internet.shop.lib.Injector;
import com.internet.shop.service.UserService;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationFilter implements Filter {
    private static final String USER_ID = "userId";
    private static Injector injector = Injector.getInstance("com.internet.shop");
    private final UserService userService = (UserService) injector.getInstance(UserService.class);
    private final Set<String> urls = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        urls.add("/login");
        urls.add("/users/registration");
        urls.add("/");
        urls.add("/inject-data");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String url = req.getServletPath();
        if (urls.contains(url)) {
            chain.doFilter(req, resp);
            return;
        }

        Long userId = (Long) req.getSession().getAttribute(USER_ID);
        if (userId == null || userService.get(userId) == null) {
            resp.sendRedirect("/login");
            return;
        }

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
    }
}
