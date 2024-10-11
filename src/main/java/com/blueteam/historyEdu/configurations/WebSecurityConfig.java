package com.blueteam.historyEdu.configurations;

import com.blueteam.historyEdu.components.JwtTokenUtils;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.filters.JwtTokenFilter;
import com.blueteam.historyEdu.responses.ResponseObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig implements WebMvcConfigurer {
    private final JwtTokenFilter jwtTokenFilter;
    private final JwtTokenUtils jwtTokenUtils;
    private final ObjectMapper objectMapper;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api-docs/**", "swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(
                                String.format("%s/users/register", apiPrefix),
                                String.format("%s/users/login", apiPrefix),
                                String.format("%s/users/generate-secret-key", apiPrefix),
                                String.format("%s/users/block-or-enable/**", apiPrefix),
                                String.format("%s/payment/**", apiPrefix),
                                String.format("%s/users/oauth2/facebook", apiPrefix),
                                String.format("%s/users/oauth2/google", apiPrefix),
                                String.format("%s/forgot-password/**", apiPrefix),
                                String.format("%s/courses/get-all", apiPrefix),
                                String.format("%s/courses/getDetail/**", apiPrefix),
                                String.format("%s/payments/**", apiPrefix),
                                String.format("%s/checkouts/**", apiPrefix),
                                String.format("%s/orders/cancel", apiPrefix),
                                String.format("%s/orders/success", apiPrefix),
                                String.format("/confirm-webhook", apiPrefix),
                                String.format("%s/courses/get-all-free-course", apiPrefix),
                                String.format("%s/courses/get-all-paid-course", apiPrefix)

                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, authException) -> {
                            ResponseObject errorResponse = ResponseObject.builder()
                                    .status(HttpStatus.UNAUTHORIZED)
                                    .message(authException.getMessage())
                                    .build();

                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            objectMapper.writeValue(response.getWriter(), errorResponse);
                        })
                );
//                .oauth2Login(oauth2 -> oauth2
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(customOAuth2UserService))
//                        .successHandler((request, response, authentication) -> {
//                            User user = (User) authentication.getPrincipal();
//                            String token = jwtTokenUtils.generateToken(user);
//                            response.setContentType("application/json");
//                            response.setStatus(HttpServletResponse.SC_OK);
//                            response.getWriter().write("{\"token\":\"" + token + "\"}");
//                        })
//                        .failureHandler((request, response, exception) -> {
//                            response.setContentType("application/json");
//                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                            response.getWriter().write("{\"error\":\"" + exception.getMessage() + "\"}");
//                        })
//                );
        http.securityMatcher(String.valueOf(EndpointRequest.toAnyEndpoint()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token", "content-disposition"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}