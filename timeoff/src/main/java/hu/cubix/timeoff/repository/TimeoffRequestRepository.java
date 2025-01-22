package hu.cubix.timeoff.repository;

import hu.cubix.timeoff.model.TimeoffRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeoffRequestRepository extends JpaRepository<TimeoffRequest, Long> {
}
