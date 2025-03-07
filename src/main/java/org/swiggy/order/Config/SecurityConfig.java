package org.swiggy.order.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.swiggy.order.Repository.UserRepository;
import org.swiggy.order.Security.ApiKeyFilter;
import org.swiggy.order.Service.UserService.CustomUserDetailService;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/users").permitAll() // Public access for user registration
                        .requestMatchers(HttpMethod.POST, "/users/*/orders").authenticated() // Basic Auth for creating orders
                        .requestMatchers(HttpMethod.PUT, "/restaurants/*/orders/*/status").authenticated() // API Key validation handled in filter
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults()) // Enable Basic Auth
                .addFilterBefore(new ApiKeyFilter(), UsernamePasswordAuthenticationFilter.class) // Custom API key filter
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService customUserDetailsService(UserRepository userRepository) {
        return new CustomUserDetailService(userRepository);
    }


}
