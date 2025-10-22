package com.docker.config;

import java.util.Arrays;
import java.util.Set;

import com.docker.security.CustomAuthenticationFailureHandler;
import com.docker.security.CustomAuthenticationSuccessHandler;
import com.docker.security.LoginAttemptFilter;
import com.docker.security.CsrfTokenLogger;
import com.docker.security.ContentSecurityPolicyFilter;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.core.env.Environment;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final UserDetailsService userDetailsService;
	private final CustomAuthenticationSuccessHandler successHandler;
	private final CustomAuthenticationFailureHandler failureHandler;
	private final LoginAttemptFilter loginAttemptFilter;
	private final CsrfTokenLogger csrfTokenLogger;
	private final ContentSecurityPolicyFilter cspFilter;

    public SecurityConfig(UserDetailsService userDetailsService,
                          CustomAuthenticationSuccessHandler successHandler,
                          CustomAuthenticationFailureHandler failureHandler,
                          LoginAttemptFilter loginAttemptFilter,
                          CsrfTokenLogger csrfTokenLogger,
                          ContentSecurityPolicyFilter cspFilter,
                          Environment environment) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.loginAttemptFilter = loginAttemptFilter;
        this.csrfTokenLogger = csrfTokenLogger;
        this.cspFilter = cspFilter;
        this.environment = environment;
    }

	/**
	 * NOTE: Utilisation de web.ignoring() pour les fichiers statiques
	 * car .permitAll() cause des problèmes avec CORS
	 * IMPORTANT: /vue/** est inclus car c'est une SPA qui gère sa propre auth via API
	 */
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring()
			.requestMatchers("/uploaded-avatars/**")
			.requestMatchers("/uploaded-photos/**")
			.requestMatchers("/uploaded-music/**")
			.requestMatchers("/uploaded-videos/**")
			.requestMatchers("/css/**")
			.requestMatchers("/js/**")
			.requestMatchers("/images/**")
			.requestMatchers("/vue/**")
			.requestMatchers("/favicon.ico");
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

	/**
	 * Configuration CORS pour permettre les requêtes depuis le frontend Vue.js
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// Origines autorisées
		configuration.setAllowedOrigins(Arrays.asList(
			"http://localhost:5173",  // Vue.js dev server
			"http://localhost:8106"   // Backend
		));

		// Méthodes HTTP autorisées
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		// En-têtes autorisés
		configuration.setAllowedHeaders(Arrays.asList("*"));

		// Permettre les credentials (cookies, authorization headers)
		configuration.setAllowCredentials(true);

		// Durée de cache de la réponse preflight (OPTIONS)
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

    private final Environment environment;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authenticationProvider(authenticationProvider());

		// Activer CORS avec la configuration définie dans corsConfigurationSource()
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

		// ========================================
		// EN-TÊTES DE SÉCURITÉ HTTP
		// ========================================
        final boolean isProd = java.util.Arrays.asList(environment.getActiveProfiles()).contains("prod");

        http.headers(headers -> headers
            // X-Frame-Options: Protège contre les attaques de clickjacking
            .frameOptions(frameOptions -> frameOptions.deny())

			// X-Content-Type-Options: Empêche le navigateur de deviner le type MIME
			.contentTypeOptions(contentTypeOptions -> {})

			// X-XSS-Protection: Protection XSS (obsolète mais garde la compatibilité)
			.xssProtection(xssProtection -> xssProtection
				.headerValue(org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
			)

            // Strict-Transport-Security (HSTS): Force HTTPS en production
            .httpStrictTransportSecurity(hsts -> {
                if (isProd) {
                    hsts.maxAgeInSeconds(31536000).includeSubDomains(true).preload(true);
                }
            })
        
            // Referrer-Policy: Contrôle les informations envoyées dans l'en-tête Referer
            .referrerPolicy(referrerPolicy -> referrerPolicy
                .policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            )

			// Permissions-Policy: Contrôle les fonctionnalités du navigateur
			.permissionsPolicy(permissionsPolicy -> permissionsPolicy
				.policy("camera=(), microphone=(), geolocation=()")
			)
		);

		// Désactiver CSRF pour les fichiers statiques et l'API publique
		http.csrf(csrf -> csrf
			.csrfTokenRequestHandler(new org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler())
			.ignoringRequestMatchers(
				"/uploaded-music/**",
				"/uploaded-photos/**",
				"/uploaded-videos/**",
				"/uploaded-avatars/**",
				"/api/**" // Désactive CSRF pour toute l'API (y compris /api/logout)
			)
		);

		http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
				// Routes ADMIN
				.requestMatchers("/admin/**").hasRole("ADMIN")

				// Routes USER (USER ou ADMIN)
				.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")

				// Routes COMMENTS (utilisateurs authentifiés)
				.requestMatchers("/comments/**").hasAnyRole("USER", "ADMIN")

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
						"/forgot-password", // Demande de réinitialisation de mot de passe
						"/reset-password", // Réinitialisation de mot de passe avec token
						"/privacy-policy", // Politique de confidentialité RGPD
						"/mentions-legales", // Mentions légales
						"/cookies", // Politique des cookies
						"/css/**", // Fichiers CSS
						"/js/**", // Fichiers JavaScript
						"/images/**", // Images statiques
						"/vue/**", // Application Vue.js buildée
						"/uploaded-photos/**", // Photos uploadées
						"/uploaded-music/**", // Musique uploadée
						"/uploaded-videos/**", // Vidéos uploadées
						"/uploaded-avatars/**", // Avatars utilisateurs - IMPORTANT !
						"/api/**", // API REST publique
						"/swagger-ui/**", // Documentation Swagger UI
						"/v3/api-docs/**", // Documentation OpenAPI JSON
						"/swagger-ui.html", // Page principale Swagger UI
						"/actuator/health", // Health check endpoint (public)
						"/actuator/info" // Info endpoint (public)
				).permitAll()

				// Tout le reste nécessite une authentification
				.anyRequest().authenticated())
				// Configuration CSRF : activé par défaut (pas de configuration supplémentaire nécessaire)
				// Spring Security active automatiquement la protection CSRF pour toutes les requêtes POST/PUT/DELETE
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.successHandler(successHandler)
						.failureHandler(failureHandler)
						.permitAll())
				.logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll());

		// ========================================
		// GESTION DES SESSIONS
		// ========================================
		http.sessionManagement(session -> session
			// IF_REQUIRED: créer une session si nécessaire (défaut)
			.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
			// Invalider la session lors du changement d'ID de session
			.sessionFixation().migrateSession()
			// Maximum 1 session simultanée par utilisateur
			.maximumSessions(1)
			.maxSessionsPreventsLogin(false) // La nouvelle session remplace l'ancienne
			.expiredUrl("/login?expired")
		);

		// Ajouter le filtre anti-brute force AVANT le filtre d'authentification
		http.addFilterBefore(loginAttemptFilter, UsernamePasswordAuthenticationFilter.class);

		// Ajouter le filtre de logging CSRF pour le debugging (en développement)
		http.addFilterAfter(csrfTokenLogger, UsernamePasswordAuthenticationFilter.class);

		// Ajouter le filtre Content Security Policy
		http.addFilterAfter(cspFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
