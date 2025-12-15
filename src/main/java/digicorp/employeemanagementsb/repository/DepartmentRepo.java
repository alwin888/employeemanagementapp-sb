package digicorp.employeemanagementsb.repository;
import digicorp.employeemanagementsb.model.Department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Department} entities.
 * <p>
 * This repository provides standard CRUD operations and query methods
 * for the {@link Department} entity via {@link JpaRepository}.
 * </p>
 *
 * <p>
 * No custom query methods are defined because basic operations such as
 * retrieving all departments are already provided by the inherited
 * {@link JpaRepository} methods (e.g. {@link #findAll()}).
 * </p>
 *
 * @see JpaRepository
 * @see Department
 */
@Repository
public interface DepartmentRepo extends JpaRepository<Department, String> {
    //method not needed because findAll() given by default

}
