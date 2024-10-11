package com.blueteam.historyEdu.filters;

import com.blueteam.historyEdu.components.JwtTokenUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (isBypassToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is missing or invalid.");
                return;
            }

            final String token = authHeader.substring(7);
            if (token.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is empty.");
                return;
            }

            final Map<String, String> identifier = jwtTokenUtils.extractIdentifier(token);
            if (identifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String emailOrPhone = identifier.get("email") != null ? identifier.get("email") : identifier.get("phoneNumber");
                UserDetails userDetails = userDetailsService.loadUserByUsername(emailOrPhone);

                if (jwtTokenUtils.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
                    return;
                }
            }
            filterChain.doFilter(request, response);


        } catch (JwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token parsing error: " + e.getMessage());
        } catch (UsernameNotFoundException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found: " + e.getMessage());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage());
        }

    }




    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/users/generate-secret-key", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/block-or-enable/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/forgot-password/send-otp/**", apiPrefix), "POST"),
                Pair.of(String.format("%s/forgot-password/verify-otp/**", apiPrefix), "POST"),
                Pair.of(String.format("%s/forgot-password/change-password/**", apiPrefix), "POST"),
                Pair.of(String.format("%s/courses/get-all", apiPrefix), "GET"),
                Pair.of(String.format("%s/courses/getDetail/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/payments/**", apiPrefix), "POST"),
                Pair.of(String.format("%s/checkouts/**", apiPrefix), "POST"),
                Pair.of(String.format("%s/orders/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/orders/cancel", apiPrefix), "POST"),
                Pair.of(String.format("%s/orders/success", apiPrefix), "POST"),
                Pair.of(String.format("%s/courses/get-all-paid-course", apiPrefix), "GET"),
                Pair.of(String.format("%s/courses/get-all-free-course", apiPrefix), "GET"),
                Pair.of("/api-docs", "GET"),
                Pair.of("/api-docs/**", "GET"),
                Pair.of("/swagger-resources", "GET"),
                Pair.of("/swagger-resources/**", "GET"),
                Pair.of("/configuration/ui", "GET"),
                Pair.of("/configuration/security", "GET"),
                Pair.of("/v3/api-docs/**", "GET"),
                Pair.of("/swagger-ui.html", "GET"),
                Pair.of("/swagger-ui/**", "GET")
        );
        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();
        for (Pair<String, String> token : bypassTokens) {
            String path = token.getFirst();
            String method = token.getSecond();
            // Check if the request path and method match any pair in the bypassTokens list
            if (requestPath.matches(path.replace("**", ".*"))
                    && requestMethod.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
}
