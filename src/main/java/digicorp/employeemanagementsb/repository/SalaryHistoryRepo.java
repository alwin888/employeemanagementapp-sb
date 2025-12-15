package digicorp.employeemanagementsb.repository;

import digicorp.employeemanagementsb.model.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface for accessing {@link SalaryHistory} entities.
 * <p>
 * This repository manages historical salary records for employees and provides
 * standard CRUD operations through {@link JpaRepository}, along with
 * custom queries for retrieving the most recent salary information.
 * </p>
 */
public interface SalaryHistoryRepo extends JpaRepository<SalaryHistory, Integer> {

    /**
     * Retrieves the latest salary record for a given employee.
     * <p>
     * This method selects the {@link SalaryHistory} entry associated with the specified
     * employee number where the {@code toDate} matches the provided maximum date.
     * Results are ordered by {@code fromDate} in descending order to ensure the
     * most recent salary record is returned.
     * </p>
     *
     * @param empNo   the employee number whose salary history is being queried
     * @param maxDate the maximum {@code toDate}, typically representing the
     *                current or latest salary period
     * @return an {@link Optional} containing the latest {@link SalaryHistory}
     *         if found; otherwise {@link Optional#empty()}
     */
    @Query("""
        SELECT s 
        FROM SalaryHistory s 
        WHERE s.employee.empNo = :empNo 
            AND s.toDate = :maxDate
        ORDER BY s.id.fromDate DESC
    """)
    Optional<SalaryHistory> findLatestSalary(
            @Param("empNo") int empNo,
            @Param("maxDate") LocalDate maxDate
    );
}