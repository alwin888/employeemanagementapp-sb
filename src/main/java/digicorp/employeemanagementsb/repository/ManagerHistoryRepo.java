package digicorp.employeemanagementsb.repository;

import digicorp.employeemanagementsb.model.DeptManager;
import digicorp.employeemanagementsb.model.DeptManagerId;
import digicorp.employeemanagementsb.model.TitleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ManagerHistoryRepo extends JpaRepository<DeptManager, DeptManagerId> {

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