package com.example.ProjectApp.security;

import com.example.ProjectApp.gateway.ApiGateway;
import com.example.ProjectApp.model.EmployeeDto;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class ApiAuthenticationProvider implements AuthenticationProvider {

    private final ApiGateway api;

    // ID du poste qui donne le rôle RH
    private static final long RH_POSTE_ID = 29L;

    public ApiAuthenticationProvider(ApiGateway api) {
        this.api = api;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String email = authentication.getName() == null ? "" : authentication.getName().trim();
        String password = authentication.getCredentials() == null ? "" : authentication.getCredentials().toString();

        if (email.isEmpty() || password.isEmpty()) {
            // Laisse la main aux autres providers (InMemory)
            return null;
        }

        // 1) Récupérer l'employé par email via l'API existante
        EmployeeDto user = api.listEmployees().stream()
                .filter(e -> e.getEmail() != null && e.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);

        if (user == null) {
            // Pas trouvé côté API -> on laisse la main aux autres providers
            return null;
        }

        // 2) Comparaison du mot de passe (en clair ici)
        String stored = user.getPassword();
        if (stored == null || !stored.equals(password)) {
            // Mauvais mot de passe -> on laisse la main aux autres providers
            return null;
        }

        // 3) Déterminer le rôle à partir de l'ID du poste
        Long pid = user.getPosteId();
        if (pid == null && user.getPoste() != null) {
            // Si ton DTO a aussi un objet poste, on récupère son id en secours
            pid = user.getPoste().getId();
        }

        String role = (pid != null && pid.longValue() == RH_POSTE_ID) ? "ROLE_RH" : "ROLE_EMP";

        return new UsernamePasswordAuthenticationToken(
                email,                      // principal
                null,                       // credentials non conservés
                List.of(new SimpleGrantedAuthority(role))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
