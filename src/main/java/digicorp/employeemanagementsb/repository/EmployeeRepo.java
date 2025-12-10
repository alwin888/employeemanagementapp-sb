package digicorp.employeemanagementsb.repository;

import digicorp.employeemanagementsb.dto.EmployeeRecordDTO;
import digicorp.employeemanagementsb.model.Employee;

import digicorp.employeemanagementsb.dto.EmployeeRecordDTO;
import digicorp.employeemanagementsb.model.DeptEmployee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EmployeeRepo extends PagingAndSortingRepository<DeptEmployee, Long> {

    @Query("""
        SELECT new digicorp.employeemanagementsb.dto.EmployeeRecordDTO(
            e.empNo, e.firstName, e.lastName, e.hireDate
        )
        FROM DeptEmployee de
        JOIN de.employee e
        WHERE de.id.deptNo = :deptNo
        ORDER BY e.empNo ASC
    """)
    List<EmployeeRecordDTO> findByDepartment(
            @Param("deptNo") String deptNo,
            Pageable pageable
    );
}