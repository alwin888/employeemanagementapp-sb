package digicorp.employeemanagementsb.repository;

import digicorp.employeemanagementsb.model.DeptEmployee;
import digicorp.employeemanagementsb.model.DeptEmployeeId;
import digicorp.employeemanagementsb.model.TitleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DepartmentHistoryRepo extends JpaRepository<DeptEmployee, DeptEmployeeId> {

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