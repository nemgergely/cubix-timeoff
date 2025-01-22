package hu.cubix.timeoff.repository;

import hu.cubix.timeoff.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
