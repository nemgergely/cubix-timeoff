package hu.cubix.timeoff.specification;

import hu.cubix.timeoff.enums.RequestStatus;
import hu.cubix.timeoff.model.Employee_;
import hu.cubix.timeoff.model.TimeoffRequest;
import hu.cubix.timeoff.model.TimeoffRequest_;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TimeoffRequestSpecification {

    private TimeoffRequestSpecification() {}

    public static Specification<TimeoffRequest> statusMatches(String requestStatus) {
        return (root, cq, cb) ->
            cb.equal(root.get(TimeoffRequest_.requestStatus), RequestStatus.valueOf(requestStatus));
    }

    public static Specification<TimeoffRequest> requesterOrApproverNameStartsWith(String namePrefix) {
        return requesterNameStartsWith(namePrefix).or(approverNameStartsWith(namePrefix));
    }

    public static Specification<TimeoffRequest> requestDateBetween(LocalDate from, LocalDate to) {
        return (root, cq, cb) ->
            cb.between((root.get(TimeoffRequest_.requestDateTime)).as(LocalDate.class), from, to);
    }

    public static Specification<TimeoffRequest> intervalIntersectsWith(LocalDate from, LocalDate to) {
        return startDateBetween(from, to).or(endDateBetween(from, to));
    }

    private static Specification<TimeoffRequest> startDateBetween(LocalDate from, LocalDate to) {
        return (root, cq, cb) ->
            cb.between((root.get(TimeoffRequest_.startDate)), from, to);
    }

    private static Specification<TimeoffRequest> endDateBetween(LocalDate from, LocalDate to) {
        return (root, cq, cb) ->
            cb.between((root.get(TimeoffRequest_.endDate)), from, to);
    }

    private static Specification<TimeoffRequest> requesterNameStartsWith(String namePrefix) {
        return (root, cq, cb) ->
            cb.like(cb.lower(root.get(TimeoffRequest_.requester).get(Employee_.NAME)), namePrefix.toLowerCase() + "%");
    }

    private static Specification<TimeoffRequest> approverNameStartsWith(String namePrefix) {
        return (root, cq, cb) ->
            cb.like(cb.lower(root.get(TimeoffRequest_.approver).get(Employee_.NAME)), namePrefix.toLowerCase() + "%");
    }
}
