package digicorp.employeemanagementsb.repository;

import digicorp.employeemanagementsb.model.DeptManager;
import digicorp.employeemanagementsb.model.DeptManagerId;
import digicorp.employeemanagementsb.model.TitleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface for accessing {@link DeptManager} entities.
 * <p>
 * This repository manages the historical records of department managers and
 * provides CRUD operations via {@link JpaRepository}, along with custom
 * queries to retrieve the latest manager assignment for an employee.
 * </p>
 *
 * <p>
 * The {@link DeptManager} entity uses a composite primary key represented by
 * {@link DeptManagerId}.
 * </p>
 */
public interface ManagerHistoryRepo extends JpaRepository<DeptManager, DeptManagerId> {

    /**
     * Retrieves the most recent manager record for a given employee.
     * <p>
     * This query selects a {@link DeptManager} entry associated with the specified
     * employee number where the {@code toDate} matches the provided maximum date.
     * It is commonly used to determine the employee's current or latest manager
     * assignment.
     * </p>
     *
     * @param empNo   the employee number whose manager history is being queried
     * @param maxDate the maximum {@code toDate}, typically representing the latest
     *                or current manager assignment
     * @return an {@link Optional} containing the latest {@link DeptManager} record
     *         if found; otherwise {@link Optional#empty()}
     */
    @Query("""
        SELECT t
        FROM DeptManager t 
        WHERE t.employee.empNo = :empNo 
            AND t.toDate = :maxDate
        AND t.toDate = :maxDate
    """)
    Optional<DeptManager> findLatestManager(
            @Param("empNo") int empNo,
            @Param("maxDate") LocalDate maxDate
    );

}