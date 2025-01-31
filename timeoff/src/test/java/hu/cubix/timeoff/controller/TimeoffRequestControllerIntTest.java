package hu.cubix.timeoff.controller;

import hu.cubix.timeoff.dto.TimeoffCriteriaDto;
import hu.cubix.timeoff.dto.TimeoffEvaluationDto;
import hu.cubix.timeoff.dto.TimeoffRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static hu.cubix.timeoff.enums.RequestStatus.REJECTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class TimeoffRequestControllerIntTest {

    @Autowired
    WebTestClient webTestClient;

    public static final String API_TIMEOFF = "/api/timeoff";

    @Test
    void testCreateTimeoffRequest() {
        // ARRANGE
        LocalDate from = LocalDate.of(2022, 5, 1);
        LocalDate to = LocalDate.of(2022, 5, 4);
        TimeoffRequestDto newTimeoffRequestDto = new TimeoffRequestDto(null, from, to);
        List<TimeoffRequestDto> timeoffRequestsBefore = getAllTimeoffRequests();

        // ACT
        createPostTimeoffRequest(newTimeoffRequestDto);

        // ASSERT
        List<TimeoffRequestDto> timeoffRequestsAfter = getAllTimeoffRequests();
        TimeoffRequestDto addedTimeoffRequestDto = timeoffRequestsAfter.get(timeoffRequestsAfter.size() - 1);

        assertThat(timeoffRequestsAfter.subList(0, timeoffRequestsBefore.size()))
            .usingRecursiveFieldByFieldElementComparatorOnFields("startDate", "endDate")
            .containsExactlyElementsOf(timeoffRequestsBefore);
        assertThat(addedTimeoffRequestDto)
            .usingRecursiveComparison()
            .ignoringFields("id", "requestDateTime", "requestStatus", "requester", "approver")
            .isEqualTo(newTimeoffRequestDto);
        assertEquals("A Aladar", addedTimeoffRequestDto.getRequester().getName());
        assertEquals("Manager One", addedTimeoffRequestDto.getApprover().getName());
    }

    @Test
    void testUpdateTimeoffRequest() {
        // ARRANGE
        List<TimeoffRequestDto> timeoffRequestsBefore = getAllTimeoffRequests();
        Long id = 1L;
        LocalDate from = LocalDate.of(2024, 5, 1);
        LocalDate to = LocalDate.of(2024, 5, 4);
        TimeoffRequestDto updateTimeoffRequestDto = new TimeoffRequestDto(id, from, to);

        // ACT
        updatePutTimeoffRequest(updateTimeoffRequestDto);

        // ASSERT
        List<TimeoffRequestDto> timeoffRequestsAfter = getAllTimeoffRequests();
        Optional<TimeoffRequestDto> updatedDto = timeoffRequestsAfter
            .stream()
            .filter(tor -> tor.getId().equals(id))
            .findFirst();
        assertTrue(updatedDto.isPresent());
        assertEquals(id, updatedDto.get().getId());
        assertThat(updatedDto.get())
            .usingRecursiveComparison()
            .ignoringFields("requestDateTime", "requestStatus", "requester", "approver")
            .isEqualTo(updateTimeoffRequestDto);
        assertEquals(timeoffRequestsBefore.size(), timeoffRequestsAfter.size());
    }

    @Test
    void testEvaluateTimeoffRequest() {
        // ARRANGE
        List<TimeoffRequestDto> timeoffRequestsBefore = getAllTimeoffRequests();
        Long id = 4L;
        TimeoffEvaluationDto timeoffEvaluationDto = new TimeoffEvaluationDto(id, REJECTED);

        // ACT
        evaluatePatchTimeoffRequest(timeoffEvaluationDto);

        // ASSERT
        List<TimeoffRequestDto> timeoffRequestsAfter = getAllTimeoffRequests();
        Optional<TimeoffRequestDto> evaluatedDto = timeoffRequestsAfter
            .stream()
            .filter(tor -> tor.getId().equals(id))
            .findFirst();
        assertTrue(evaluatedDto.isPresent());
        assertEquals(id, evaluatedDto.get().getId());
        assertEquals(REJECTED, evaluatedDto.get().getRequestStatus());
        assertEquals(timeoffRequestsBefore.size(), timeoffRequestsAfter.size());
    }

    @Test
    void testDeleteTimeoffRequest() {
        // ARRANGE
        List<TimeoffRequestDto> timeoffRequestsBefore = getAllTimeoffRequests();

        // ACT
        removeDeleteTimeoffRequest();

        // ASSERT
        List<TimeoffRequestDto> timeoffRequestsAfter = getAllTimeoffRequests();
        assertEquals(timeoffRequestsBefore.size() - 1, timeoffRequestsAfter.size());
        assertTrue(timeoffRequestsAfter.stream().noneMatch(tor -> tor.getId().equals(5L)));
    }

    private void createPostTimeoffRequest(TimeoffRequestDto newTimeoffRequestDto) {
        webTestClient
            .post()
            .uri(
                API_TIMEOFF
                .concat("?requesterId=3"))
            .bodyValue(newTimeoffRequestDto)
            .exchange()
            .expectStatus()
            .isOk();
    }

    private void removeDeleteTimeoffRequest() {
        webTestClient
            .delete()
            .uri(
                API_TIMEOFF
                .concat("?id=5"))
            .exchange()
            .expectStatus()
            .isOk();
    }

    private void updatePutTimeoffRequest(TimeoffRequestDto updateTimeoffRequestDto) {
        webTestClient
            .put()
            .uri(API_TIMEOFF)
            .bodyValue(updateTimeoffRequestDto)
            .exchange()
            .expectStatus()
            .isOk();
    }

    private void evaluatePatchTimeoffRequest(TimeoffEvaluationDto timeoffEvaluationDto) {
        webTestClient
            .patch()
            .uri(
                API_TIMEOFF
                .concat("//")
                .concat("evaluate"))
            .bodyValue(timeoffEvaluationDto)
            .exchange()
            .expectStatus()
            .isOk();
    }

    private List<TimeoffRequestDto> getAllTimeoffRequests() {
        List<TimeoffRequestDto> allTimeoffRequests = webTestClient
            .post()
            .uri(
                API_TIMEOFF
                .concat("//")
                .concat("filtered"))
            .bodyValue(new TimeoffCriteriaDto())
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(TimeoffRequestDto.class)
            .returnResult()
            .getResponseBody();

        return allTimeoffRequests.stream().sorted(Comparator.comparing(TimeoffRequestDto::getId)).toList();
    }
}
