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
        SELECT s 
        FROM DeptEmployee s 
        WHERE s.employee.empNo = :empNo 
        ORDER BY s.fromDate DESC
    """)
    Optional<DeptEmployee> findLatestDepartment(@Param("empNo") int empNo);

    @Query("""
        SELECT t
        FROM DeptEmployee t 
        WHERE t.employee.empNo = :empNo 
        AND t.toDate = :maxDate
    """)
    List<DeptEmployee> findCurrentDepartments(
            @Param("empNo") int empNo,
            @Param("maxDate") LocalDate maxDate
    );;
}