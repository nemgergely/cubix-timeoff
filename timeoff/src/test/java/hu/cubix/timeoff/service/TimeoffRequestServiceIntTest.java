package hu.cubix.timeoff.service;

import hu.cubix.timeoff.dto.TimeoffCriteriaDto;
import hu.cubix.timeoff.enums.RequestStatus;
import hu.cubix.timeoff.model.TimeoffRequest;
import hu.cubix.timeoff.repository.TimeoffRequestRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static hu.cubix.timeoff.enums.RequestStatus.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TimeoffRequestServiceIntTest {

    @Autowired
    private TimeoffRequestService timeoffRequestService;

    @Autowired
    private TimeoffRequestRepository timeoffRequestRepository;

    @Order(1)
    @ParameterizedTest
    @CsvSource(value = {
        "All with approver Manager One, null, Manager O, null, null, null, null",
        "All with approver Manager Two, null, Manager T, null, null, null, null",
        "All created between 2024-01-15 and 2025-01-15, null, null, 2024-01-15, 2025-01-15, null, null",
        "All created between 2025-01-15 and 2026-01-15, null, null, 2025-01-15, 2026-01-15, null, null",
        "All active between 2025-06-30 and 2025-09-30, null, null, null, null, 2025-06-30, 2025-09-30",
        "All active between 2025-09-30 and 2025-12-30, null, null, null, null, 2025-09-30, 2025-12-30",
        "All rejected by Manager One, REJECTED, Manager O, null, null, null, null",
        "All rejected by Manager Two, REJECTED, Manager T, null, null, null, null",
    }, nullValues = "null")
    void testFindTimeoffRequestBySpecifications(
        String caseName,
        String requestStatus,
        String namePrefix,
        String createdFrom,
        String createdTo,
        String activeFrom,
        String activeTo
    ) {

        // ARRANGE
        TimeoffCriteriaDto timeoffCriteriaDto = new TimeoffCriteriaDto(
            StringUtils.hasLength(requestStatus) ? requestStatus : null,
            StringUtils.hasLength(namePrefix) ? namePrefix : null,
            StringUtils.hasLength(createdFrom) ? LocalDate.parse(createdFrom) : null,
            StringUtils.hasLength(createdTo) ? LocalDate.parse(createdTo) : null,
            StringUtils.hasLength(activeFrom) ? LocalDate.parse(activeFrom) : null,
            StringUtils.hasLength(activeTo) ? LocalDate.parse(activeTo) : null
        );
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        // ACT
        Page<TimeoffRequest> pagedAndFilteredTimeoffRequests =
            timeoffRequestService.findTimeoffRequestsBySpecification(timeoffCriteriaDto, pageable);

        // ASSERT
        List<TimeoffRequest> timeoffRequests = pagedAndFilteredTimeoffRequests.getContent();
        switch (caseName) {
            case "All with approver Manager One":
                assertEquals(3, timeoffRequests.size());
                assertTrue(timeoffRequests.stream().allMatch(timeoffRequest ->
                    timeoffRequest.getApprover().getId().equals(1L)));
                assertTrue(timeoffRequests.stream()
                    .map(request -> request.getRequester().getId())
                    .toList()
                    .containsAll(List.of(3L, 4L, 5L)));
                break;
            case "All with approver Manager Two":
                assertEquals(3, timeoffRequests.size());
                assertTrue(timeoffRequests.stream().allMatch(timeoffRequest ->
                    timeoffRequest.getApprover().getId().equals(2L)));
                assertTrue(timeoffRequests.stream()
                    .map(request -> request.getRequester().getId())
                    .toList()
                    .containsAll(List.of(13L, 14L, 15L)));
                break;
            case "All created between 2024-01-15 and 2025-01-15", "All created between 2025-01-15 and 2026-01-15":
                assertEquals(3, timeoffRequests.size());
                assertTrue(timeoffRequests.stream()
                    .allMatch(timeoffRequest ->
                        timeoffRequest.getRequestDateTime().toLocalDate().isAfter(LocalDate.parse(createdFrom)) &&
                            timeoffRequest.getRequestDateTime().toLocalDate().isBefore(LocalDate.parse(createdTo))
                    )
                );
                break;
            case "All active between 2025-06-30 and 2025-09-30", "All active between 2025-09-30 and 2025-12-30":
                assertEquals(3, timeoffRequests.size());
                assertTrue(timeoffRequests.stream()
                    .allMatch(timeoffRequest ->
                        (timeoffRequest.getStartDate().isAfter(LocalDate.parse(activeFrom)) &&
                            timeoffRequest.getStartDate().isBefore(LocalDate.parse(activeTo))) ||
                            (timeoffRequest.getEndDate().isAfter(LocalDate.parse(activeFrom)) &&
                                timeoffRequest.getEndDate().isBefore(LocalDate.parse(activeTo)))
                    )
                );
                break;
            case "All rejected by Manager One":
                assertEquals(1, timeoffRequests.size());
                assertEquals(5L, timeoffRequests.get(0).getRequester().getId());
                assertEquals(REJECTED, timeoffRequests.get(0).getRequestStatus());
                break;
            case "All rejected by Manager Two":
                assertEquals(1, timeoffRequests.size());
                assertEquals(15L, timeoffRequests.get(0).getRequester().getId());
                assertEquals(REJECTED, timeoffRequests.get(0).getRequestStatus());
                break;
            default:
                break;
        }
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource(value = {
        "Requester not found, 30, 2025-08-22, 2025-08-25",
        "Manager not found, 1, 2025-08-22, 2025-08-25",
        "Valid, 13, 2025-08-22, 2025-08-25",
    })
    void testCreateTimeoffRequest(String caseName, Long requesterId, String startDateString, String endDateString) {
        // ARRANGE
        LocalDate startDate = LocalDate.parse(startDateString);
        LocalDate endDate = LocalDate.parse(endDateString);
        TimeoffRequest timeoffRequest = new TimeoffRequest(null, startDate, endDate);
        NoSuchElementException thrown;

        // ACT - ASSERT
        switch (caseName) {
            case "Requester not found":
                thrown = assertThrows(
                    NoSuchElementException.class,
                    () -> timeoffRequestService.createTimeoffRequest(requesterId, timeoffRequest)
                );
                assertTrue(thrown.getMessage().contains("Requester employee not found"));
                break;
            case "Manager not found":
                thrown = assertThrows(
                    NoSuchElementException.class,
                    () -> timeoffRequestService.createTimeoffRequest(requesterId, timeoffRequest)
                );
                assertTrue(thrown.getMessage().contains("There is no manager found who can approve this request"));
                break;
            case "Valid":
                TimeoffRequest newTimeoffRequest = timeoffRequestService.createTimeoffRequest(requesterId, timeoffRequest);
                assertEquals(PENDING, newTimeoffRequest.getRequestStatus());
                assertEquals(requesterId, newTimeoffRequest.getRequester().getId());
                assertEquals(startDate, newTimeoffRequest.getStartDate());
                assertEquals(endDate, newTimeoffRequest.getEndDate());
                break;
            default:
                break;
        }
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource(value = {
        "Time off request not found, 10, 2025-10-22, 2025-10-25",
        "Time off request already evaluated, 2, 2025-10-22, 2025-10-25",
        "Valid, 1, 2025-10-22, 2025-10-25",
    })
    void testUpdateTimeoffRequest(String caseName, Long id, String startDateString, String endDateString) {
        // ARRANGE
        LocalDate startDate = LocalDate.parse(startDateString);
        LocalDate endDate = LocalDate.parse(endDateString);
        TimeoffRequest timeoffRequest = new TimeoffRequest(id, startDate, endDate);
        RuntimeException thrown;

        // ACT - ASSERT
        switch (caseName) {
            case "Time off request not found":
                thrown = assertThrows(
                    NoSuchElementException.class,
                    () -> timeoffRequestService.updateTimeoffRequest(timeoffRequest)
                );
                assertTrue(thrown.getMessage().contains("Timeoff request not found"));
                break;
            case "Time off request already evaluated":
                thrown = assertThrows(
                    UnsupportedOperationException.class,
                    () -> timeoffRequestService.updateTimeoffRequest(timeoffRequest)
                );
                assertTrue(thrown.getMessage().contains("This request was already evaluated"));
                break;
            case "Valid":
                TimeoffRequest updatedTimeoffRequest = timeoffRequestService.updateTimeoffRequest(timeoffRequest);
                assertEquals(PENDING, updatedTimeoffRequest.getRequestStatus());
                assertEquals(id, updatedTimeoffRequest.getId());
                assertEquals(startDate, updatedTimeoffRequest.getStartDate());
                assertEquals(endDate, updatedTimeoffRequest.getEndDate());
                break;
            default:
                break;
        }
    }

    @Order(4)
    @ParameterizedTest
    @CsvSource(value = {
        "Time off request not found, 10, REJECTED",
        "Invalid evaluation, 1, PENDING",
        "Time off request already evaluated, 2, REJECTED",
        "Valid, 1, APPROVED",
    })
    void testEvaluateTimeoffRequest(String caseName, Long id, String evaluation) {
        // ARRANGE
        RuntimeException thrown;
        RequestStatus requestStatus = RequestStatus.valueOf(evaluation);

        // ACT - ASSERT
        switch (caseName) {
            case "Time off request not found":
                thrown = assertThrows(
                    NoSuchElementException.class,
                    () -> timeoffRequestService.evaluateTimeoffRequest(id, requestStatus)
                );
                assertTrue(thrown.getMessage().contains("Timeoff request not found"));
                break;
            case "Invalid evaluation":
                thrown = assertThrows(
                    UnsupportedOperationException.class,
                    () -> timeoffRequestService.evaluateTimeoffRequest(id, requestStatus)
                );
                assertTrue(thrown.getMessage().contains("Invalid evaluation"));
                break;
            case "Time off request already evaluated":
                thrown = assertThrows(
                    UnsupportedOperationException.class,
                    () -> timeoffRequestService.evaluateTimeoffRequest(id, requestStatus)
                );
                assertTrue(thrown.getMessage().contains("This request was already evaluated"));
                break;
            case "Valid":
                TimeoffRequest updatedTimeoffRequest = timeoffRequestService.evaluateTimeoffRequest(id, requestStatus);
                assertEquals(APPROVED, updatedTimeoffRequest.getRequestStatus());
                assertEquals(id, updatedTimeoffRequest.getId());
                break;
            default:
                break;
        }
    }

    @Order(5)
    @Test
    void testDeleteTimeoffRequest() {
        // ACT
        timeoffRequestService.deleteTimeoffRequest(6L);

        // ARRANGE
        List<TimeoffRequest> timeoffRequests = timeoffRequestRepository.findAll();
        assertTrue(timeoffRequests.stream().noneMatch(tor -> tor.getId().equals(6L)));
    }
}
