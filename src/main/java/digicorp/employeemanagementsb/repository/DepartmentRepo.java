package digicorp.employeemanagementsb.repository;
import digicorp.employeemanagementsb.model.Department;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class DepartmentRepo {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Department> findAll() {
        return entityManager.createQuery(
                        "SELECT d FROM Department d ORDER BY d.deptNo", Department.class)
                .getResultList();
    }


}
