package digicorp.employeemanagementsb.repository;

import digicorp.employeemanagementsb.model.DeptManager;
import digicorp.employeemanagementsb.model.DeptManagerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ManagerHistoryRepo extends JpaRepository<DeptManager, DeptManagerId> {

    @Query("""
        SELECT s 
        FROM DeptManager s 
        WHERE s.employee.empNo = :empNo 
        ORDER BY s.fromDate DESC
    """)
    List<DeptManager> findManagerByEmployee(@Param("empNo") int empNo);
}