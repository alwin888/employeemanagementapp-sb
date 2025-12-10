package digicorp.employeemanagementsb.repository;
import digicorp.employeemanagementsb.model.Department;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepo extends JpaRepository<Department, String> {
    //method not needed because findAll() given by default

}
