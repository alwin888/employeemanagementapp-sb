package digicorp.employeemanagementsb.repository;

import digicorp.employeemanagementsb.model.DeptEmployee;
import digicorp.employeemanagementsb.model.DeptEmployeeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for accessing {@link DeptEmployee} entities.
 * <p>
 * This repository manages the department history of employees and provides
 * CRUD operations through {@link JpaRepository}, as well as custom queries
 * for retrieving department assignment data.
 * </p>
 *
 * <p>
 * The {@link DeptEmployee} entity uses a composite primary key represented by
 * {@link DeptEmployeeId}.
 * </p>
 */
public interface DepartmentHistoryRepo extends JpaRepository<DeptEmployee, DeptEmployeeId> {

    /**
     * Retrieves the most recent department assignments for a given employee.
     * <p>
     * This method selects {@link DeptEmployee} records associated with the specified
     * employee number where the {@code toDate} matches the provided maximum date.
     * It is typically used to determine the employee's current or latest department(s).
     * </p>
     *
     * @param empNo   the employee number whose department history is being queried
     * @param maxDate the maximum {@code toDate}, usually representing the "current"
     *                or latest department assignment
     * @return a list of {@link DeptEmployee} records matching the given criteria;
     *         may be empty if no matching records are found
     */
    @Query("""
        SELECT t
        FROM DeptEmployee t 
        WHERE t.employee.empNo = :empNo 
            AND t.toDate = :maxDate
        AND t.toDate = :maxDate
    """)
    List<DeptEmployee> findLatestDepartments(
            @Param("empNo") int empNo,
            @Param("maxDate") LocalDate maxDate
    );;
}