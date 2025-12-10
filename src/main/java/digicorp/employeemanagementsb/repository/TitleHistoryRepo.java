package digicorp.employeemanagementsb.repository;

import digicorp.employeemanagementsb.model.SalaryHistory;
import digicorp.employeemanagementsb.model.TitleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TitleHistoryRepo extends JpaRepository<TitleHistory, String> {

    @Query("""
        SELECT t
        FROM TitleHistory t 
        WHERE t.employee.empNo = :empNo 
        ORDER BY t.id.fromDate DESC
    """)
    List<TitleHistory> findTitleByEmployee(@Param("empNo") int empNo);
}
