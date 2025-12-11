package digicorp.employeemanagementsb.repository;

import digicorp.employeemanagementsb.model.SalaryHistory;
import digicorp.employeemanagementsb.model.TitleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalaryHistoryRepo extends JpaRepository<SalaryHistory, Integer> {

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