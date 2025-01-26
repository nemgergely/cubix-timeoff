package hu.cubix.timeoff.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import hu.cubix.timeoff.jsonviews.Views;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class TimeoffRequestDto {

    @JsonView(Views.RequestView.class)
    private Long id;

    @JsonView(Views.RequestView.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonView(Views.RequestView.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @JsonView(Views.RequestView.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestDateTime;

    @JsonView(Views.RequestView.class)
    private String requestStatus;

    @JsonView(Views.RequestView.class)
    private EmployeeDto requester;

    @JsonView(Views.RequestView.class)
    private EmployeeDto approver;

    public TimeoffRequestDto(Long id, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
