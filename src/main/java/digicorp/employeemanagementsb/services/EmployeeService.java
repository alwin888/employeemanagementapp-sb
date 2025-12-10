package digicorp.employeemanagementsb.services;

import digicorp.employeemanagementsb.dto.EmployeeRecordDTO;
import digicorp.employeemanagementsb.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepo repo;

    public List<EmployeeRecordDTO> findByDepartment(String deptNo, int page) {
        int pageSize = 20;
        PageRequest pageable = PageRequest.of(page - 1, pageSize); // page is 0-indexed
        return repo.findByDepartment(deptNo, pageable);
    }
}