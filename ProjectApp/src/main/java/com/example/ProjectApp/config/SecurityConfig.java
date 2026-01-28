package com.example.ProjectApp.config;

import com.example.ProjectApp.gateway.ApiGateway;
import com.example.ProjectApp.security.ApiAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final RoleBasedSuccessHandler successHandler;
    private final ApiGateway apiGateway;

    public SecurityConfig(RoleBasedSuccessHandler successHandler, ApiGateway apiGateway) {
        this.successHandler = successHandler;
        this.apiGateway = apiGateway;
    }

    // === Fallback InMemory ===
    @Bean
    public UserDetailsService userDetailsService() {
        var rh = User.withUsername("rh@bank.local")
                .password("{noop}rh123")
                .roles("RH")
                .build();
        var emp = User.withUsername("emp@bank.local")
                .password("{noop}emp123")
                .roles("EMP")
                .build();
        return new InMemoryUserDetailsManager(rh, emp);
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService uds) {
        var dbProvider = new ApiAuthenticationProvider(apiGateway);
        var inMemoryProvider = new DaoAuthenticationProvider();
        inMemoryProvider.setUserDetailsService(uds);
        return new ProviderManager(dbProvider, inMemoryProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        http
                .authenticationManager(authManager)
                .authorizeHttpRequests(auth -> auth
                        // On autorise login, error, les assets et le bot
                        .requestMatchers("/login", "/error", "/css/**", "/js/**", "/images/**", "/bot/**").permitAll()
                        .requestMatchers("/rh/**").hasRole("RH")
                        .requestMatchers("/me/**").hasAnyRole("RH", "EMP")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                // --- CORRECTION FINALE ICI ---
                // On garde le CSRF actif (pour que login.html fonctionne)
                // MAIS on l'ignore spécifiquement pour l'URL du bot (pour que le JS fonctionne)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/bot/**"));

        return http.build();
    }
}