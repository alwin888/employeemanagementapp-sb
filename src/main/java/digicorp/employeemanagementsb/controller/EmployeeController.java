package digicorp.employeemanagementsb.controller;

import java.util.List;

import digicorp.employeemanagementsb.dto.EmployeeRecordDTO;
import digicorp.employeemanagementsb.services.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/department")
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
