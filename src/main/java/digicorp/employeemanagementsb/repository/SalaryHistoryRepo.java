package digicorp.employeemanagementsb.repository;

import digicorp.employeemanagementsb.model.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalaryHistoryRepo extends JpaRepository<SalaryHistory, Integer> {

    @Query("""
        SELECT s 
        FROM SalaryHistory s 
        WHERE s.employee.empNo = :empNo 
        ORDER BY s.id.fromDate DESC
    """)
    List<SalaryHistory> findSalaryByEmployee(@Param("empNo") int empNo);
}