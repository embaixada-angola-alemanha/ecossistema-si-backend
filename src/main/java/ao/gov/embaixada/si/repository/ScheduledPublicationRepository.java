package ao.gov.embaixada.si.repository;

import ao.gov.embaixada.si.entity.ScheduledPublication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ScheduledPublicationRepository extends JpaRepository<ScheduledPublication, UUID> {

    List<ScheduledPublication> findByExecutedFalseAndScheduledAtBefore(Instant now);

    List<ScheduledPublication> findByPageId(UUID pageId);
}
