package hu.cubix.timeoff.repository;

import hu.cubix.timeoff.model.TimeoffRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TimeoffRequestRepository extends
    JpaRepository<TimeoffRequest, Long>,
    JpaSpecificationExecutor<TimeoffRequest> {
}
