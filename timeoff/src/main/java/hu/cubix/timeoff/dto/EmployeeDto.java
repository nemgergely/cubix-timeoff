package hu.cubix.timeoff.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import hu.cubix.timeoff.jsonviews.Views;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeDto {

    @JsonView(Views.RequestView.class)
    private String name;

    private EmployeeDto manager;

    private List<TimeoffRequestDto> requests;

    private List<EmployeeDto> subordinates;

    private List<TimeoffRequestDto> approvals;
}
