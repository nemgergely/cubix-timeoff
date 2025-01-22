package hu.cubix.timeoff.service;

import hu.cubix.timeoff.enums.RequestStatus;
import hu.cubix.timeoff.model.Employee;
import hu.cubix.timeoff.model.TimeoffRequest;
import hu.cubix.timeoff.repository.EmployeeRepository;
import hu.cubix.timeoff.repository.TimeoffRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class TimeoffRequestService {

    private final TimeoffRequestRepository timeoffRequestRepository;
    private final EmployeeRepository employeeRepository;

    public TimeoffRequest createTimeoffRequest(Long requesterId, TimeoffRequest timeoffRequest) {
        Optional<Employee> optRequester = employeeRepository.findById(requesterId);
        if (optRequester.isEmpty()) {
            throw new NoSuchElementException("Requester employee not found");
        }
        Employee requester = optRequester.get();
        Employee approver = requester.getManager();
        if (approver == null) {
            throw new NoSuchElementException("There is no manager found who can approve this request");
        }
        timeoffRequest.setRequestStatus(RequestStatus.PENDING);
        timeoffRequest.setRequestDateTime(LocalDateTime.now());
        timeoffRequest.setRequester(requester);
        timeoffRequest.setApprover(approver);
        return timeoffRequestRepository.save(timeoffRequest);
    }
}
