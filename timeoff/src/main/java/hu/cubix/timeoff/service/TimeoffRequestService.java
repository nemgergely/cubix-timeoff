package hu.cubix.timeoff.service;

import hu.cubix.timeoff.dto.TimeoffCriteriaDto;
import hu.cubix.timeoff.enums.RequestStatus;
import hu.cubix.timeoff.model.Employee;
import hu.cubix.timeoff.model.TimeoffRequest;
import hu.cubix.timeoff.repository.EmployeeRepository;
import hu.cubix.timeoff.repository.TimeoffRequestRepository;
import hu.cubix.timeoff.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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
            specs = specs.and(intervalIntersection(activeFrom, activeTo));
        }

        return timeoffRequestRepository.findAll(specs, pageable);
    }

    @Transactional
    public TimeoffRequest createTimeoffRequest(Long requesterId, TimeoffRequest timeoffRequest) {
        Employee requester = checkEmployeeExisting(requesterId);
        checkIfAuthorizedRequest(requesterId, false);
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

    @Transactional
    public TimeoffRequest evaluateTimeoffRequest(Long id, RequestStatus requestStatus) {
        TimeoffRequest timeoffRequest = checkTimeoffRequestExisting(id);
        if ((requestStatus != APPROVED && requestStatus != REJECTED)) {
            throw new UnsupportedOperationException("Invalid evaluation");
        }
        checkIfAuthorizedRequest(timeoffRequest.getApprover().getId(), true);
        checkTimeoffRequestStatus(timeoffRequest);
        timeoffRequest.setRequestStatus(requestStatus);
        return timeoffRequest;
    }

    @Transactional
    public TimeoffRequest updateTimeoffRequest(TimeoffRequest updatedTimeoffRequest) {
        TimeoffRequest timeoffRequest = checkTimeoffRequestExisting(updatedTimeoffRequest.getId());
        checkIfAuthorizedRequest(timeoffRequest.getRequester().getId(), false);
        checkTimeoffRequestStatus(timeoffRequest);
        if (updatedTimeoffRequest.getRequestStatus() == null || updatedTimeoffRequest.getRequestStatus() != PENDING) {
            timeoffRequest.setRequestStatus(PENDING);
        }
        timeoffRequest.setStartDate(updatedTimeoffRequest.getStartDate());
        timeoffRequest.setEndDate(updatedTimeoffRequest.getEndDate());
        timeoffRequest.setRequestDateTime(LocalDateTime.now());

        return timeoffRequest;
    }

    @Transactional
    public void deleteTimeoffRequest(Long id) {
        TimeoffRequest timeoffRequest = checkTimeoffRequestExisting(id);
        checkIfAuthorizedRequest(timeoffRequest.getRequester().getId(), false);
        timeoffRequestRepository.delete(timeoffRequest);
    }

    private void checkIfAuthorizedRequest(Long id, boolean isEvaluation) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
        String errorMessage = isEvaluation ?
            "This request's evaluation belongs to another manager" :
            "This request belongs to another user";
        if (!id.equals(userDetails.getId())) {
            throw new AuthorizationDeniedException(errorMessage);
        }
    }

    private TimeoffRequest checkTimeoffRequestExisting(Long id) {
        Optional<TimeoffRequest> optTimeoffRequest = timeoffRequestRepository.findById(id);
        if (optTimeoffRequest.isEmpty()) {
            throw new NoSuchElementException("Timeoff request not found");
        }
        return optTimeoffRequest.get();
    }

    private Employee checkEmployeeExisting(Long id) {
        Optional<Employee> optEmployee = employeeRepository.findById(id);
        if (optEmployee.isEmpty()) {
            throw new NoSuchElementException("Employee not found");
        }
        return optEmployee.get();
    }

    private void checkTimeoffRequestStatus(TimeoffRequest timeoffRequest) {
        if (timeoffRequest.getRequestStatus() != PENDING) {
            throw new UnsupportedOperationException("This request was already evaluated");
        }
    }
}
