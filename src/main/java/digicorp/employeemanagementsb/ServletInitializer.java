package digicorp.employeemanagementsb;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Servlet initializer for deploying the application as a traditional
 * WAR file to an external servlet container.
 * <p>
 * This class is required when the Spring Boot application is packaged
 * as a WAR and deployed to servers such as Tomcat, Jetty, or WildFly,
 * instead of being run as a standalone JAR.
 * </p>
 *
 * <p>
 * It configures the {@link EmployeemanagementsbApplication} class as
 * the primary source for the Spring application context.
 * </p>
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EmployeemanagementsbApplication.class);
    }

}
