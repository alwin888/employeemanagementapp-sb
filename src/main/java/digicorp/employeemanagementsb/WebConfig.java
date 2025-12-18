package digicorp.employeemanagementsb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class for the application.
 * <p>
 * This class customizes Spring MVC behavior by defining global web-related
 * configurations, such as Cross-Origin Resource Sharing (CORS) rules.
 * </p>
 */
@Configuration
public class WebConfig {

    /**
     * Configures global CORS (Cross-Origin Resource Sharing) settings.
     * <p>
     * This configuration allows HTTP requests from the specified frontend
     * origin to access all backend endpoints. It is typically required when
     * the frontend (e.g. React) and backend (Spring Boot) are hosted on
     * different ports or domains.
     * </p>
     *
     * The following rules are applied:
     * <ul>
     *   <li>All endpoints ({@code /**}) are accessible</li>
     *   <li>Requests are allowed only from {@code http://localhost:3000}</li>
     *   <li>Supported HTTP methods include GET, POST, PUT, DELETE, and OPTIONS</li>
     * </ul>
     *
     * @return a {@link WebMvcConfigurer} that applies the defined CORS rules
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            /**
             * Adds CORS mappings to the application.
             *
             * @param registry the {@link CorsRegistry} used to register CORS configuration
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
            }
        };
    }
}
