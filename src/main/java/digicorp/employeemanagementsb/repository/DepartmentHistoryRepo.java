package digicorp.employeemanagementsb.repository;

import digicorp.employeemanagementsb.model.DeptEmployee;
import digicorp.employeemanagementsb.model.DeptEmployeeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DepartmentHistoryRepo extends JpaRepository<DeptEmployee, DeptEmployeeId> {

    @Query("""
        SELECT s 
        FROM DeptEmployee s 
        WHERE s.employee.empNo = :empNo 
        ORDER BY s.fromDate DESC
    """)
    List<DeptEmployee> findDepartmentsByEmployee(@Param("empNo") int empNo);
}