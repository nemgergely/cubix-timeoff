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

    public static Specification<TimeoffRequest> intervalIntersection(LocalDate from, LocalDate to) {
        return partialIntervalIntersection(from, to).or(completeIntervalIntersection(from, to));
    }

    private static Specification<TimeoffRequest> partialIntervalIntersection(LocalDate from, LocalDate to) {
        Specification<TimeoffRequest> timeOffStartsBetweenFromAndToDates =
            (root, cq, cb) ->
            cb.between((root.get(TimeoffRequest_.startDate)), from, to);
        Specification<TimeoffRequest> timeOffEndsBetweenFromAndToDates =
            (root, cq, cb) ->
            cb.between((root.get(TimeoffRequest_.endDate)), from, to);

        return timeOffStartsBetweenFromAndToDates.or(timeOffEndsBetweenFromAndToDates);
    }

    private static Specification<TimeoffRequest> completeIntervalIntersection(LocalDate from, LocalDate to) {
        Specification<TimeoffRequest> timeOffStartsBeforeFromDate =
            (root, cq, cb) ->
            cb.lessThanOrEqualTo((root.get(TimeoffRequest_.startDate)), from);
        Specification<TimeoffRequest> timeOffEndsAfterToDate =
            (root, cq, cb) ->
            cb.greaterThanOrEqualTo((root.get(TimeoffRequest_.endDate)), to);
        Specification<TimeoffRequest> timeOffStartsAfterFromDate =
            (root, cq, cb) ->
            cb.greaterThanOrEqualTo((root.get(TimeoffRequest_.startDate)), from);
        Specification<TimeoffRequest> timeOffEndsBeforeToDate =
            (root, cq, cb) ->
            cb.lessThanOrEqualTo((root.get(TimeoffRequest_.endDate)), to);

        return (timeOffStartsBeforeFromDate.and(timeOffEndsAfterToDate)).or
            (timeOffStartsAfterFromDate.and(timeOffEndsBeforeToDate));
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
