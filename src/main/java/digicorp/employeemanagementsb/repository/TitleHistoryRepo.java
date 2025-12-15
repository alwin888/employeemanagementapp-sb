package digicorp.employeemanagementsb.repository;

import digicorp.employeemanagementsb.model.TitleHistory;
import digicorp.employeemanagementsb.model.TitleHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface for accessing {@link TitleHistory} entities.
 * <p>
 * This repository manages the historical job titles of employees and provides
 * standard CRUD operations through {@link JpaRepository}, as well as
 * custom queries for retrieving title history information.
 * </p>
 *
 * <p>
 * The {@link TitleHistory} entity uses a composite primary key represented by
 * {@link TitleHistoryId}.
 * </p>
 */
public interface TitleHistoryRepo extends JpaRepository<TitleHistory, TitleHistoryId> {

    /**
     * Retrieves the most recent job title for a given employee.
     * <p>
     * This method selects the {@link TitleHistory} record associated with the specified
     * employee number where the {@code toDate} matches the provided maximum date.
     * Results are ordered by {@code fromDate} in descending order to ensure the
     * latest title assignment is returned.
     * </p>
     *
     * @param empNo   the employee number whose latest title is being queried
     * @param maxDate the maximum {@code toDate}, typically representing the current
     *                or active title record
     * @return an {@link Optional} containing the latest {@link TitleHistory} if found;
     *         otherwise, {@link Optional#empty()}
     */
    @Query("""
    SELECT t 
    FROM TitleHistory t
    WHERE t.employee.empNo = :empNo
        AND t.toDate = :maxDate
    ORDER BY t.id.fromDate DESC
    """)
    Optional<TitleHistory> findLatestTitle(
            @Param("empNo") int empNo,
            @Param("maxDate") LocalDate maxDate
    );

}
