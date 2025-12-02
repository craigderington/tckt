package com.kitchen.tckt.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tcktOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8088");
        localServer.setDescription("Local Development Server");

        Contact contact = new Contact();
        contact.setName("Craig Derington");
        contact.setUrl("https://github.com/craigderington");

        Info info = new Info()
                .title("tckt - Kitchen Ticket Queue API")
                .version("1.0.0")
                .description("Distributed kitchen order queue system for demonstrating Kubernetes clustering behavior. " +
                             "Tracks orders across multiple pods and nodes with server-side rendering and real-time updates.")
                .contact(contact);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
