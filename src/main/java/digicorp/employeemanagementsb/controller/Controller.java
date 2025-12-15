package digicorp.employeemanagementsb.controller;

import java.util.List;

import digicorp.employeemanagementsb.dto.EmployeeRecordDTO;
import digicorp.employeemanagementsb.dto.PromotionRequestDTO;
import digicorp.employeemanagementsb.model.Department;
import digicorp.employeemanagementsb.model.Employee;
import digicorp.employeemanagementsb.repository.DepartmentRepo;
import digicorp.employeemanagementsb.repository.EmployeeRepo;
import digicorp.employeemanagementsb.services.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Sort;

/**
 * REST controller that exposes HTTP endpoints for managing employees
 * and departments.
 * <p>
 * This controller acts as the entry point for client requests and delegates
 * business logic to the {@link EmployeeService} and data access operations
 * to repository interfaces.
 * </p>
 *
 * <p>
 * All responses are returned as JSON and appropriate HTTP status codes
 * are used for error handling.
 * </p>
 */
@RestController
public class Controller {

    private final DepartmentRepo departmentRepo;
    private final EmployeeRepo employeeRepo;
    private final EmployeeService employeeService;

    /**
     * Constructs the controller with required dependencies.
     *
     * @param departmentRepo repository for department data access
     * @param employeeRepo   repository for employee data access
     * @param employeeService service containing employee business logic
     */
    public Controller(DepartmentRepo departmentRepo,
                             EmployeeRepo employeeRepo,
                             EmployeeService employeeService) {
        this.departmentRepo = departmentRepo;
        this.employeeRepo = employeeRepo;
        this.employeeService = employeeService;
    }

    /**
     * Retrieves all departments in ascending order of department number.
     *
     * @return a list of all {@link Department} entities
     */
    @GetMapping("/department")
    public List<Department> listDepartment() {
        return departmentRepo.findAll(Sort.by(Sort.Direction.ASC, "deptNo"));
    }

    /**
     * Retrieves a single employee by employee number.
     * <p>
     * Validates that the employee number is a positive integer before querying
     * the database.
     * </p>
     *
     * @param empNoStr the employee number provided as a path variable
     * @return the {@link Employee} if found
     * @throws ResponseStatusException if the input is invalid or employee is not found
     */
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

    /**
     * Retrieves a paginated list of employees belonging to a specific department.
     * <p>
     * The department number must follow the format {@code d###} (e.g. d001).
     * Pagination starts from page 1.
     * </p>
     *
     * @param deptNo the department number
     * @param page   the page number (1-based)
     * @return a list of {@link EmployeeRecordDTO} for the given department
     * @throws ResponseStatusException if validation fails
     */
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

    /**
     * Promotes an employee based on the provided promotion request.
     * <p>
     * Promotion logic such as department change, role update, or salary adjustment
     * is handled by the service layer.
     * </p>
     *
     * @param requestDTO the promotion request payload
     * @return the updated {@link Employee} after promotion
     */
    @PostMapping("/employees/promote")
    public Employee promote(@RequestBody PromotionRequestDTO requestDTO) {
        // Delegate promotion logic to the service
        return employeeService.promoteEmployee(requestDTO);
    }

}
