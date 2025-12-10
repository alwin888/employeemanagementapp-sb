package digicorp.employeemanagementsb.repository;

import digicorp.employeemanagementsb.model.SalaryHistory;
import digicorp.employeemanagementsb.model.TitleHistory;
import digicorp.employeemanagementsb.model.TitleHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TitleHistoryRepo extends JpaRepository<TitleHistory, TitleHistoryId> {

    @Query("""
    SELECT t 
    FROM TitleHistory t
    WHERE t.employee.empNo = :empNo
    ORDER BY t.id.fromDate DESC
    """)
    Optional<TitleHistory> findLatestTitle(@Param("empNo") int empNo);

    @Query("""
        SELECT t
        FROM TitleHistory t 
        WHERE t.employee.empNo = :empNo 
        AND t.toDate = :maxDate
    """)
    List<TitleHistory> findCurrentTitles(
            @Param("empNo") int empNo,
            @Param("maxDate") LocalDate maxDate
    );;
}
