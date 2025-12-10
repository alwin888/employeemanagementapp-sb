package digicorp.employeemanagementsb.services;

import digicorp.employeemanagementsb.dto.EmployeeRecordDTO;
import digicorp.employeemanagementsb.repository.*;
import digicorp.employeemanagementsb.dto.PromotionRequestDTO;
import digicorp.employeemanagementsb.model.Employee;
import digicorp.employeemanagementsb.model.SalaryHistory;
import digicorp.employeemanagementsb.model.TitleHistory;
import digicorp.employeemanagementsb.model.Department;
import digicorp.employeemanagementsb.model.DeptEmployee;
import digicorp.employeemanagementsb.model.DeptEmployeeId;
import digicorp.employeemanagementsb.model.SalaryHistoryId;
import digicorp.employeemanagementsb.model.TitleHistoryId;
import digicorp.employeemanagementsb.model.DeptManager;
import digicorp.employeemanagementsb.model.DeptManagerId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private SalaryHistoryRepo salaryHistoryRepo;

    @Autowired
    private TitleHistoryRepo titleHistoryRepo;

    @Autowired
    private DepartmentHistoryRepo departmentHistoryRepo;

    @Autowired ManagerHistoryRepo managerHistoryRepo;

    public List<EmployeeRecordDTO> findByDepartment(String deptNo, int page) {
        int pageSize = 20;
        PageRequest pageable = PageRequest.of(page - 1, pageSize); // page is 0-indexed
        return employeeRepo.findByDepartment(deptNo, pageable);
    }

    public Employee promoteEmployee(PromotionRequestDTO dto){

        // Validation of request DTO

        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "{\"error\":\"Request body is missing or invalid JSON\"}");
        }

        if (dto.getEmpNo() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "{\"error\":\"empNo must be a positive integer\"}");
        }

        if (dto.getNewTitle() == null || dto.getNewTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title cannot be blank");
        }

        if (dto.getFromDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "{\"error\":\"fromDate is required (yyyy-MM-dd)\"}");
        }

        LocalDate newFromDate = dto.getFromDate();
        int empNo = dto.getEmpNo();

        // SALARY VALIDATION
        if (dto.getNewSalary() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "{\"error\":\"salary must be greater than 0\"}");
        }

        // DEPT VALIDATION ("d001" format)
        if (dto.getNewDeptNo() == null ||
                !dto.getNewDeptNo().trim().matches("^[dD][0-9]{3}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "{\"error\":\"deptNo must match pattern dXXX (e.g., d001, D005)\"}");
        }

        // Normalize to lowercase for DB consistency
        String deptNoNormalized = dto.getNewDeptNo().trim().toLowerCase();

        //make sure employee can be found
        Employee emp = employeeRepo.findByEmpNo(empNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        // ===============================================================
        // DATE VALIDATION MUST BE AFTER LATEST (SALARY, TITLE, DEPARTMENT)
        // ===============================================================

        // 1. Latest salary
        LocalDate latestSalaryDate =
                salaryHistoryRepo.findLatestSalary(empNo)
                        .map(s -> s.getId().getFromDate())
                        .orElse(LocalDate.MIN);

        // 2. Latest title
        LocalDate latestTitleDate =
                titleHistoryRepo.findLatestTitle(empNo)
                        .map(t -> t.getId().getFromDate())
                        .orElse(LocalDate.MIN);

        // 3. Latest department assignment
        LocalDate latestDeptDate =
                departmentHistoryRepo.findLatestDepartment(empNo)
                        .map(d -> d.getFromDate())
                        .orElse(LocalDate.MIN);

        // Get the maximum of all latest dates
        LocalDate latestAnyDate = Stream.of(latestSalaryDate, latestTitleDate, latestDeptDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.MIN);

        // Validation
        if (!newFromDate.isAfter(latestAnyDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "fromDate must be AFTER latest existing date: " + latestAnyDate
            );
        }

        // ===============================================================
        // PROMOTION LOGIC (1.SALARY, 2.TITLE, 3.DEPARTMENT, 4. MANAGER)
        // ===============================================================

        // --- 4a. SALARY ---
        SalaryHistory newSalary = new SalaryHistory();
        SalaryHistoryId salaryId = new SalaryHistoryId(emp.getEmpNo(), newFromDate);
        newSalary.setId(salaryId);
        newSalary.setEmployee(emp);
        newSalary.setSalary(dto.getNewSalary());
        newSalary.setToDate(LocalDate.of(9999, 1, 1));

        // Close previous salary
        salaryHistoryRepo.findLatestSalary(empNo).ifPresent(lastSalary -> {
            lastSalary.setToDate(newFromDate.minusDays(1));
            salaryHistoryRepo.save(lastSalary);
        });
        salaryHistoryRepo.save(newSalary);

        // --- 4b. TITLE ---
        List<TitleHistory> currentTitles = titleHistoryRepo.findCurrentTitles(
                empNo,
                LocalDate.of(9999, 1, 1));
        for (TitleHistory t : currentTitles) {
            t.setToDate(newFromDate.minusDays(1));
            titleHistoryRepo.save(t);
        }

        TitleHistoryId newTitleId = new TitleHistoryId(empNo, dto.getNewTitle(), newFromDate);
        TitleHistory newTitle = new TitleHistory();
        newTitle.setId(newTitleId);
        newTitle.setEmployee(emp);
        newTitle.setToDate(LocalDate.of(9999, 1, 1));
        titleHistoryRepo.save(newTitle);

        // --- 4c. DEPARTMENT ---
        List<DeptEmployee> currentDepts = departmentHistoryRepo.findCurrentDepartments(
                empNo,
                LocalDate.of(9999, 1, 1));
        for (DeptEmployee d : currentDepts) {
            d.setToDate(newFromDate.minusDays(1));
            departmentHistoryRepo.save(d);
        }

        DeptEmployee newDept = new DeptEmployee();
        DeptEmployeeId deptId = new DeptEmployeeId(empNo, deptNoNormalized);
        newDept.setId(deptId);
        newDept.setEmployee(emp);
        newDept.setFromDate(newFromDate);
        newDept.setToDate(LocalDate.of(9999, 1, 1));
        departmentHistoryRepo.save(newDept);

        // --- 4d. MANAGER (optional) ---
        if (dto.isManager()) {
            managerHistoryRepo.findLatestManager(
                    empNo,
                    LocalDate.of(9999, 1, 1)).
                    ifPresent(lastMgr -> {
                lastMgr.setToDate(newFromDate.minusDays(1));
                managerHistoryRepo.save(lastMgr);
            });

            DeptManagerId managerId = new DeptManagerId(empNo, deptNoNormalized);
            DeptManager newMgr = new DeptManager();
            newMgr.setId(managerId);
            newMgr.setEmployee(emp);
            newMgr.setFromDate(newFromDate);
            newMgr.setToDate(LocalDate.of(9999, 1, 1));
            managerHistoryRepo.save(newMgr);
        }

        // ======================
        // 5. SAVE EMPLOYEE
        // ======================
        employeeRepo.save(emp);
        return emp;

    }
}