package digicorp.employeemanagementsb.repository;

import digicorp.employeemanagementsb.dto.EmployeeRecordDTO;
import digicorp.employeemanagementsb.model.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Employee} entities.
 * <p>
 * This repository provides basic CRUD operations and pagination support
 * via {@link PagingAndSortingRepository}, as well as custom queries for
 * retrieving employee data in DTO form.
 * </p>
 */
public interface EmployeeRepo extends PagingAndSortingRepository<Employee, Integer> {
    /**
     * Retrieves a paginated list of employees belonging to a specific department.
     * <p>
     * This query joins the department-employee mapping with the employee entity
     * and projects the results into {@link EmployeeRecordDTO} instances.
     * Only selected fields are returned to reduce data transfer.
     * </p>
     *
     * <p>
     * Results are ordered by employee number in ascending order.
     * Pagination is controlled through the provided {@link Pageable} parameter.
     * </p>
     *
     * @param deptNo   the department number used to filter employees
     * @param pageable pagination and sorting information
     * @return a list of {@link EmployeeRecordDTO} objects representing employees
     *         in the specified department; may be empty if no employees are found
     */
    @Query("""
        SELECT new digicorp.employeemanagementsb.dto.EmployeeRecordDTO(
            e.empNo, e.firstName, e.lastName, e.hireDate
        )
        FROM DeptEmployee de
        JOIN de.employee e
        WHERE de.id.deptNo = :deptNo
        ORDER BY e.empNo ASC
    """)
    List<EmployeeRecordDTO> findByDepartment(
            @Param("deptNo") String deptNo,
            Pageable pageable
    );

    /**
     * Retrieves an employee by their employee number.
     *
     * @param empNo the unique employee number
     * @return an {@link Optional} containing the {@link Employee} if found,
     *         or {@link Optional#empty()} if no employee exists with the given number
     */
    Optional<Employee> findByEmpNo(int empNo);

    /**
     * Saves the given employee entity.
     * <p>
     * If the employee already exists, the entity is updated.
     * Otherwise, a new employee record is created.
     * </p>
     *
     * @param employee the {@link Employee} entity to be persisted
     */
    void save(Employee employee);
}