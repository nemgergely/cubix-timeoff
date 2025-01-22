package hu.cubix.timeoff.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManagerDto extends EmployeeDto {

    private List<EmployeeDto> subordinates;

    private List<TimeoffRequestDto> approvalDtos;
}
