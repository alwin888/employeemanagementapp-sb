/**
 * Root package for the Employee Management Spring Boot application.
 * <p>
 * This application provides RESTful services for managing employees,
 * departments, salaries, titles, promotions, and managerial assignments.
 * </p>
 *
 * Architectural layers:
 * <ul>
 *     <li>{@code controller} : REST API endpoints</li>
 *     <li>{@code services} : business logic and validation</li>
 *     <li>{@code repository} : data access using Spring Data JPA</li>
 *     <li>{@code model} : JPA entities and composite keys</li>
 *     <li>{@code dto} : request and response data transfer objects</li>
 * </ul>
 *
 * <p>
 * The application follows a layered architecture
 * with clear separation of concerns.
 * </p>
 */
package digicorp.employeemanagementsb;
