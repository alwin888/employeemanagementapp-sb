package digicorp.employeemanagementsb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Employee Management Spring Boot application.
 * <p>
 * This class bootstraps the application using Spring Boot’s auto-configuration,
 * component scanning, and embedded server support.
 * </p>
 *
 * The {@link SpringBootApplication} annotation enables:
 * <ul>
 *   <li>Component scanning for the {@code digicorp.employeemanagementsb} package</li>
 *   <li>Autoconfiguration of Spring context and dependencies</li>
 *   <li>Configuration through Java-based annotations</li>
 * </ul>
 *
 * <p>
 * The application is launched via the {@link #main(String[])} method.
 * </p>
 */
@SpringBootApplication
public class EmployeemanagementsbApplication {

    /**
     * Main method that serves as the starting point of the application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(EmployeemanagementsbApplication.class, args);
    }

}
