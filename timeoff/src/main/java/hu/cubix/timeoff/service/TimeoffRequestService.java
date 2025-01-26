package hu.cubix.timeoff.service;

import hu.cubix.timeoff.dto.TimeoffCriteriaDto;
import hu.cubix.timeoff.enums.RequestStatus;
import hu.cubix.timeoff.model.Employee;
import hu.cubix.timeoff.model.TimeoffRequest;
import hu.cubix.timeoff.repository.EmployeeRepository;
import hu.cubix.timeoff.repository.TimeoffRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static hu.cubix.timeoff.enums.RequestStatus.*;
import static hu.cubix.timeoff.specification.TimeoffRequestSpecification.*;

@Service
@Transactional
@AllArgsConstructor
public class TimeoffRequestService {

    private final TimeoffRequestRepository timeoffRequestRepository;
    private final EmployeeRepository employeeRepository;

    public Page<TimeoffRequest> findTimeoffRequestsBySpecification(TimeoffCriteriaDto timeoffCriteriaDto, Pageable pageable) {
        String requestStatus = timeoffCriteriaDto.getRequestStatus();
        String namePrefix = timeoffCriteriaDto.getNamePrefix();
        LocalDate createdFrom = timeoffCriteriaDto.getCreatedFrom();
        LocalDate createdTo = timeoffCriteriaDto.getCreatedTo();
        LocalDate activeFrom = timeoffCriteriaDto.getActiveFrom();
        LocalDate activeTo = timeoffCriteriaDto.getActiveTo();

        Specification<TimeoffRequest> specs = Specification.where(null);

        if (StringUtils.hasLength(requestStatus)) {
            specs = specs.and(statusMatches(requestStatus));
        }
        if (StringUtils.hasLength(namePrefix)) {
            specs = specs.and(requesterOrApproverNameStartsWith(namePrefix));
        }
        if (createdFrom != null && createdTo != null) {
            specs = specs.and(requestDateBetween(createdFrom, createdTo));
        }
        if (activeFrom != null && activeTo != null) {
            specs = specs.and(intervalIntersectsWith(activeFrom, activeTo));
        }

        return timeoffRequestRepository.findAll(specs, pageable);
    }

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
        TimeoffRequest newTimeoffRequest = timeoffRequestRepository.save(timeoffRequest);
        requester.getRequests().add(newTimeoffRequest);
        approver.getApprovals().add(newTimeoffRequest);
        employeeRepository.saveAll(List.of(requester, approver));

        return newTimeoffRequest;
    }

    public TimeoffRequest evaluateTimeoffRequest(Long id, String evaluation) {
        Optional<TimeoffRequest> optTimeoffRequest = timeoffRequestRepository.findById(id);
        if (optTimeoffRequest.isEmpty()) {
            throw new NoSuchElementException("Timeoff request not found");
        }
        if (evaluation == null ||
            (!evaluation.toUpperCase().equals(String.valueOf(APPROVED))
            && !evaluation.toUpperCase().equals(String.valueOf(REJECTED)))) {
            throw new UnsupportedOperationException("Invalid evaluation");
        }
        TimeoffRequest timeoffRequest = optTimeoffRequest.get();
        if (timeoffRequest.getRequestStatus() != PENDING) {
            throw new UnsupportedOperationException("This request was already evaluated");
        }
        timeoffRequest.setRequestStatus(RequestStatus.valueOf(evaluation.toUpperCase()));
        return timeoffRequestRepository.save(timeoffRequest);
    }

    public TimeoffRequest updateTimeoffRequest(TimeoffRequest updatedTimeoffRequest) {
        Optional<TimeoffRequest> optTimeoffRequest = timeoffRequestRepository.findById(updatedTimeoffRequest.getId());
        if (optTimeoffRequest.isEmpty()) {
            throw new NoSuchElementException("Timeoff request not found");
        }
        TimeoffRequest timeoffRequest = optTimeoffRequest.get();
        if (timeoffRequest.getRequestStatus() != PENDING) {
            throw new UnsupportedOperationException("This request was already evaluated");
        }
        if (updatedTimeoffRequest.getRequestStatus() == null || updatedTimeoffRequest.getRequestStatus() != PENDING) {
            updatedTimeoffRequest.setRequestStatus(PENDING);
        }
        Employee requester = timeoffRequest.getRequester();
        Employee approver = timeoffRequest.getApprover();
        updatedTimeoffRequest.setRequestDateTime(LocalDateTime.now());
        updatedTimeoffRequest.setRequester(requester);
        updatedTimeoffRequest.setApprover(approver);
        updatedTimeoffRequest = timeoffRequestRepository.save(updatedTimeoffRequest);
        requester.getRequests().remove(timeoffRequest);
        requester.getRequests().add(updatedTimeoffRequest);
        approver.getApprovals().remove(timeoffRequest);
        approver.getApprovals().add(updatedTimeoffRequest);
        employeeRepository.saveAll(List.of(requester, approver));

        return updatedTimeoffRequest;
    }

    public void deleteTimeoffRequest(Long id) {
        timeoffRequestRepository.deleteById(id);
    }
}
