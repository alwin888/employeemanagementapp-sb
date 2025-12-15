/**
 * Repository layer for the Employee Management Spring Boot application.
 * <p>
 * This package contains Spring Data repository interfaces responsible for
 * data access and persistence operations. These repositories abstract
 * database interactions and provide CRUD functionality, pagination,
 * sorting, and custom JPQL queries.
 * </p>
 *
 * <p>
 * The repositories in this package manage historical and current data for:
 * </p>
 * <ul>
 *   <li>Employees</li>
 *   <li>Departments</li>
 *   <li>Department assignment history</li>
 *   <li>Manager assignment history</li>
 *   <li>Salary history</li>
 *   <li>Title history</li>
 * </ul>
 *
 * <p>
 * All repositories follow the Spring Data JPA pattern and are automatically
 * detected and instantiated by Spring through component scanning.
 * </p>
 *
 * <p>
 * Custom queries are defined using JPQL via the {@code @Query} annotation
 * where domain-specific retrieval logic is required.
 * </p>
 *
 * @author Digicorp
 * @since 1.0
 */
package digicorp.employeemanagementsb.repository;
