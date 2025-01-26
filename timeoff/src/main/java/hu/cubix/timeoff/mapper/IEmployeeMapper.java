package hu.cubix.timeoff.mapper;

import hu.cubix.timeoff.dto.EmployeeDto;
import hu.cubix.timeoff.model.Employee;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IEmployeeMapper {

    @Mapping(target = "requests", ignore = true)
    @Mapping(target = "approvals", ignore = true)
    @Mapping(target = "subordinates", ignore = true)
    @Named("onlyName")
    EmployeeDto employeeToDto(Employee employee);

    @IterableMapping(qualifiedByName = "onlyName")
    List<EmployeeDto> employeesToDtos(List<Employee> employees);
}
