package digicorp.employeemanagementsb.services;

import digicorp.employeemanagementsb.dto.EmployeeRecordDTO;
import digicorp.employeemanagementsb.model.*;
import digicorp.employeemanagementsb.repository.*;
import digicorp.employeemanagementsb.dto.PromotionRequestDTO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private DepartmentRepo departmentRepo;

    @Autowired
    private SalaryHistoryRepo salaryHistoryRepo;

    @Autowired
    private TitleHistoryRepo titleHistoryRepo;

    @Autowired
    private DepartmentHistoryRepo departmentHistoryRepo;

    @Autowired
    ManagerHistoryRepo managerHistoryRepo;

    public List<EmployeeRecordDTO> findByDepartment(String deptNo, int page) {
        int pageSize = 20;
        PageRequest pageable = PageRequest.of(page - 1, pageSize); // page is 0-indexed
        return employeeRepo.findByDepartment(deptNo, pageable);
    }

    public Employee promoteEmployee(PromotionRequestDTO dto) {


        // ===============================================================
        // REQUEST BODY VALIDATION
        // ===============================================================

        if (dto == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "{\"error\":\"Request body is missing or invalid JSON\"}"
            );
        }

        // empNo
        if (dto.getEmpNo() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "{\"error\":\"empNo must be a positive integer\"}"
            );
        }

        // Title
        if (dto.getNewTitle() == null || dto.getNewTitle().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "{\"error\":\"Title cannot be blank\"}"
            );
        }

        // fromDate
        if (dto.getFromDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "{\"error\":\"fromDate is required (yyyy-MM-dd)\"}"
            );
        }

        LocalDate newFromDate = dto.getFromDate();

        // Salary
        if (dto.getNewSalary() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "{\"error\":\"salary must be greater than 0\"}"
            );
        }

        // Department
        if (dto.getDeptNo() == null ||
                !dto.getDeptNo().trim().matches("^[dD][0-9]{3}$")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "{\"error\":\"deptNo must match pattern dXXX (e.g., d001, D005)\"}"
            );
        }

        String deptNoNormalized = dto.getDeptNo().trim().toLowerCase();
        int empNo = dto.getEmpNo();



        //make sure employee can be found
        Employee emp = employeeRepo.findByEmpNo(empNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));


        // ===============================================================
        // BUSINESS DATE VALIDATION
        // Includes:
        // 1. Do NOT promote employees who already left the company
        // 2. fromDate must not overlap existing salary/title/department dates
        // ===============================================================

        // --- 1. Check if employee is still active (has ANY record with toDate = 9999-01-01) ---

        boolean activeSalary = salaryHistoryRepo
                .findLatestSalary(empNo, LocalDate.of(9999, 1, 1))
                .stream()
                .findFirst()
                .isPresent();

        boolean activeTitle = titleHistoryRepo
                .findLatestTitle(empNo, LocalDate.of(9999, 1, 1))
                .stream()
                .findFirst()
                .isPresent();

        boolean activeDept = departmentHistoryRepo
                .findLatestDepartments(empNo, LocalDate.of(9999, 1, 1))
                .stream()
                .findFirst()
                .isPresent();

        // Employee has left ONLY IF at least one of the 3 are inactive
        if (!activeSalary || !activeTitle || !activeDept) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot promote employee. Employee has already left the company."
            );
        }

        // Latest salary
        LocalDate latestSalaryDate =
                salaryHistoryRepo.findLatestSalary(empNo, LocalDate.of(9999, 1, 1))
                        .stream()
                        .findFirst()
                        .map(s -> s.getId().getFromDate())
                        .orElse(LocalDate.MIN);

        // Latest title
        LocalDate latestTitleDate =
                titleHistoryRepo.findLatestTitle(empNo, LocalDate.of(9999, 1, 1))
                        .stream()
                        .findFirst()
                        .map(t -> t.getId().getFromDate())
                        .orElse(LocalDate.MIN);

        // Latest department
        LocalDate latestDeptDate =
                departmentHistoryRepo.findLatestDepartments(empNo, LocalDate.of(9999, 1, 1))
                        .stream()
                        .findFirst()
                        .map(d -> d.getFromDate())
                        .orElse(LocalDate.MIN);

        // Find the LATEST of all three
        LocalDate latestAnyDate = Stream.of(latestSalaryDate, latestTitleDate, latestDeptDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.MIN);

        // Enforce date must be strictly after all latest dates
        if (!newFromDate.isAfter(latestAnyDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "fromDate must be AFTER latest existing date: " + latestAnyDate
            );
        }





        //===============================================================
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
        salaryHistoryRepo.findLatestSalary(empNo, LocalDate.of(9999, 1, 1))
                .stream()
                .findFirst() // pick the first one (latest, because query is ordered DESC)
                .ifPresent(lastSalary -> {
                    lastSalary.setToDate(newFromDate.minusDays(1));
                    salaryHistoryRepo.save(lastSalary);
                });

        salaryHistoryRepo.save(newSalary);


        // --- 4b. TITLE ---
        TitleHistoryId titleId = new TitleHistoryId(emp.getEmpNo(), dto.getNewTitle(), newFromDate);
        TitleHistory newTitle = new TitleHistory();
        newTitle.setId(titleId);
        newTitle.setEmployee(emp);
        newTitle.setToDate(LocalDate.of(9999, 1, 1));

        // Close previous title
        titleHistoryRepo.findLatestTitle(empNo, LocalDate.of(9999, 1, 1))
                .stream()
                .findFirst()
                .ifPresent(lastTitle -> {
                    lastTitle.setToDate(newFromDate.minusDays(1));
                    titleHistoryRepo.save(lastTitle);
                });

        titleHistoryRepo.save(newTitle);

        // --- 4c. DEPARTMENT ---
        // Fetch the Department entity from DB
        Department dept = departmentRepo.findById(deptNoNormalized)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Department not found: " + deptNoNormalized));


        DeptEmployeeId deptId = new DeptEmployeeId(empNo, deptNoNormalized);
        DeptEmployee newDept = new DeptEmployee();
        newDept.setId(deptId);
        newDept.setEmployee(emp);
        newDept.setDepartment(dept);
        newDept.setFromDate(newFromDate);
        newDept.setToDate(LocalDate.of(9999, 1, 1));

        // Close previous department assignment only if department has changed
        departmentHistoryRepo.findLatestDepartments(empNo, LocalDate.of(9999, 1, 1))
                .stream()
                .findFirst() // pick the latest one
                .ifPresent(lastDept -> {
                    String oldDeptNo = lastDept.getId().getDeptNo(); // get previous department
                    boolean departmentChanged = !deptNoNormalized.equalsIgnoreCase(oldDeptNo);

                    if (departmentChanged) {
                        lastDept.setToDate(newFromDate.minusDays(1));
                        departmentHistoryRepo.save(lastDept);
                    }
                });

        departmentHistoryRepo.save(newDept);


        // --- 4d. MANAGER (optional) ---
        if (dto.isManager()) {

            // Fetch latest manager record if exists
            Optional<DeptManager> latestMgrOpt =
                    managerHistoryRepo.findLatestManager(empNo, LocalDate.of(9999, 1, 1))
                            .stream()
                            .findFirst();

            boolean shouldClosePreviousManager = false;

            if (latestMgrOpt.isPresent()) {
                DeptManager lastMgr = latestMgrOpt.get();
                String oldMgrDeptNo = lastMgr.getId().getDeptNo();

                // Close only if department changed
                shouldClosePreviousManager = !deptNoNormalized.equalsIgnoreCase(oldMgrDeptNo);

                if (shouldClosePreviousManager) {
                    lastMgr.setToDate(newFromDate.minusDays(1));
                    managerHistoryRepo.save(lastMgr);
                }
            } else {
                // No previous manager -> nothing to close
                shouldClosePreviousManager = true;
            }

            // Now create the new manager record if needed
            // (Even if dept didn't change, we create a new record because it's a promotion event)
            DeptManagerId managerId = new DeptManagerId(empNo, deptNoNormalized);

            DeptManager newMgr = new DeptManager();
            newMgr.setId(managerId);
            newMgr.setEmployee(emp);
            newMgr.setDepartment(dept);   // REQUIRED - fixes null association problem
            newMgr.setFromDate(newFromDate);
            newMgr.setToDate(LocalDate.of(9999, 1, 1));

            managerHistoryRepo.save(newMgr);
        }

        return emp;

    }
}