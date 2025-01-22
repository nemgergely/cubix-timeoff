package hu.cubix.timeoff.model;

import hu.cubix.timeoff.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "time_off_request")
@NoArgsConstructor
@Getter
@Setter
public class TimeoffRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "request_date_time", nullable = false)
    private LocalDateTime requestDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false)
    private RequestStatus requestStatus;

    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id", nullable = false)
    private Employee requester;

    @ManyToOne
    @JoinColumn(name = "approver_id", referencedColumnName = "id", nullable = false)
    private Employee approver;

    public TimeoffRequest(LocalDate startDate, LocalDate endDate,
                          LocalDateTime requestDateTime, RequestStatus requestStatus,
                          Employee requester, Employee approver) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestDateTime = requestDateTime;
        this.requestStatus = requestStatus;
        this.requester = requester;
        this.approver = approver;
    }
}
