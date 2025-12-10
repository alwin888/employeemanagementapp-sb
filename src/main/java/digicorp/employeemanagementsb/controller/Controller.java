package digicorp.employeemanagementsb.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import digicorp.employeemanagementsb.dto.EmployeeRecordDTO;
import digicorp.employeemanagementsb.model.Department;
import digicorp.employeemanagementsb.model.Employee;
import digicorp.employeemanagementsb.repository.DepartmentRepo;
import digicorp.employeemanagementsb.repository.EmployeeRepo;
import digicorp.employeemanagementsb.services.EmployeeService;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Sort;

@RestController
public class Controller {

    private final DepartmentRepo departmentRepo;
    private final EmployeeRepo employeeRepo;
    private final EmployeeService employeeService;

    public Controller(DepartmentRepo departmentRepo,
                             EmployeeRepo employeeRepo,
                             EmployeeService employeeService) {
        this.departmentRepo = departmentRepo;
        this.employeeRepo = employeeRepo;
        this.employeeService = employeeService;
    }

    @GetMapping("/department")
    public List<Department> listDepartment() {
        return departmentRepo.findAll(Sort.by(Sort.Direction.ASC, "deptNo"));
    }

    @GetMapping("/employees/{empNo}")
    public Employee getEmployee(
        @PathVariable("empNo") String empNoStr){

        // Edge Case: validate empNo format
        if (empNoStr == null || !empNoStr.matches("\\d+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "{\"error\":\"Employee number must be a positive integer.\"}");
        }

        int empNo = Integer.parseInt(empNoStr);

        return employeeRepo.findByEmpNo(empNo).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Employee not found"
        ));
    }


    @GetMapping("/employees/by-department")
    public List<EmployeeRecordDTO> getEmployeesByDept(
            @RequestParam String deptNo,
            @RequestParam(defaultValue = "1") int page) {

        // Validate deptNo exists
        if (deptNo == null || deptNo.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "{\"error\":\"Query parameter 'deptNo' is required\"}");
        }

        deptNo = deptNo.trim();

        // Validate format: d### (anycase d + 3 digits)
        if (!deptNo.matches("(?i)^d\\d{3}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "{\"error\":\"Invalid deptNo format. Expected format 'd001', 'd002', ...\"}");
        }

        //Validate page no
        if (page <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "{\"error\":\"invalid page number\"}");
        }

        return employeeService.findByDepartment(deptNo, page);
    }
}
