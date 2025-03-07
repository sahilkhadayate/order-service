package org.swiggy.order.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-Internal-Api-Key";
    private static final String HARDCODED_API_KEY = "secret-key"; // Change this to a more secure value

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Apply API key check only for the required endpoint
        if (requestURI.startsWith("/restaurants/") && requestURI.contains("/orders/") && request.getMethod().equals("PUT")) {
            String apiKey = request.getHeader(API_KEY_HEADER);

            if (apiKey == null || !apiKey.equals(HARDCODED_API_KEY)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid API Key");
                return;
            }
        }

        // Continue filter chain if valid
        filterChain.doFilter(request, response);
    }
}
