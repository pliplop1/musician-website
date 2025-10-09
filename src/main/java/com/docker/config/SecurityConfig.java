package com.docker.config;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final UserDetailsService userDetailsService;

	public SecurityConfig(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authenticationProvider(authenticationProvider());

		http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
				// Routes ADMIN
				.requestMatchers("/admin/**").hasRole("ADMIN")

				// Routes USER (USER ou ADMIN)
				.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")

				// Routes PUBLIQUES - CORRECTION ICI
				.requestMatchers("/", // Page d'accueil
						"/biographie", // Page biographie
						"/galerie", // Page galerie
						"/musique", // Page musique
						"/videos", // Page vidéos
						"/actualites", // Page actualités
						"/contact", // Formulaire de contact
						"/register", // Page d'inscription
						"/login", // Page de connexion
						"/css/**", // Fichiers CSS
						"/js/**", // Fichiers JavaScript
						"/images/**", // Images statiques
						"/uploaded-photos/**", // Photos uploadées
						"/uploaded-music/**", // Musique uploadée
						"/uploaded-videos/**", // Vidéos uploadées
						"/api/**" // API REST publique
				).permitAll()

				// Tout le reste nécessite une authentification
				.anyRequest().authenticated())
				.formLogin(formLogin -> formLogin.loginPage("/login")
						.successHandler((request, response, authentication) -> {
							System.out.println("--- Authentication Success Handler ---");
							System.out.println("User: " + authentication.getName());
							Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
							System.out.println("Roles: " + roles);

							if (roles.contains("ROLE_ADMIN")) {
								System.out.println("Redirecting to /admin/dashboard");
								response.sendRedirect("/admin/dashboard");
							} else if (roles.contains("ROLE_USER")) {
								System.out.println("Redirecting to /user/profile");
								response.sendRedirect("/user/profile");
							} else {
								System.out.println("No specific role found, redirecting to /");
								response.sendRedirect("/");
							}
						}).permitAll())
				.logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll());

		return http.build();
	}
}