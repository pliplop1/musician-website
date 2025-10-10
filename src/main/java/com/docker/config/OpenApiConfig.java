package com.docker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration OpenAPI/Swagger pour la documentation de l'API REST
 *
 * Documentation accessible via:
 * - Swagger UI: http://localhost:8106/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8106/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI duoBlackAndWhiteOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8106");
        devServer.setDescription("Serveur de développement");

        Server prodServer = new Server();
        prodServer.setUrl("https://duoblackandwhite.com");
        prodServer.setDescription("Serveur de production");

        Contact contact = new Contact();
        contact.setEmail("dumoulin.marilyne@gmail.com");
        contact.setName("Duo Black & White");
        contact.setUrl("https://www.facebook.com/DuoBlackandWhiteMP/");

        License mitLicense = new License()
                .name("Propriétaire")
                .url("https://duoblackandwhite.com/license");

        Info info = new Info()
                .title("API REST - Duo Black & White")
                .version("1.0.0")
                .contact(contact)
                .description("API REST publique pour le site web du Duo Black & White. "
                        + "Cette API permet d'accéder à la biographie, aux concerts, photos, "
                        + "morceaux de musique et de soumettre des messages de contact.")
                .termsOfService("https://duoblackandwhite.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}
