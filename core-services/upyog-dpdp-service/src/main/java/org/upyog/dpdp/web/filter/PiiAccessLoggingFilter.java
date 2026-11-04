package org.upyog.dpdp.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.upyog.dpdp.service.AccessLogService;

import java.io.IOException;

@Component
public class PiiAccessLoggingFilter extends OncePerRequestFilter {

    private final AccessLogService accessLogService;

    public PiiAccessLoggingFilter(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(request, response);
        String path = request.getRequestURI();
        if (path != null && (path.contains("/rights/v1/") || path.contains("/consent/v1/_search"))) {
            accessLogService.logAccess(
                    request.getHeader("x-tenant-id"),
                    request.getHeader("x-user-uuid"),
                    request.getHeader("x-user-uuid"),
                    null,
                    request.getHeader("X-DPDP-Purpose"),
                    "HTTP_" + request.getMethod(),
                    path,
                    request.getRemoteAddr());
        }
    }
}
