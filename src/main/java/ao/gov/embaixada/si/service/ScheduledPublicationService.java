package ao.gov.embaixada.si.service;

import ao.gov.embaixada.si.entity.ScheduledPublication;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.repository.ScheduledPublicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ScheduledPublicationService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledPublicationService.class);

    private final ScheduledPublicationRepository scheduledRepo;
    private final PageService pageService;

    public ScheduledPublicationService(ScheduledPublicationRepository scheduledRepo,
                                        PageService pageService) {
        this.scheduledRepo = scheduledRepo;
        this.pageService = pageService;
    }

    public ScheduledPublication schedule(UUID pageId, Instant scheduledAt) {
        ScheduledPublication sp = new ScheduledPublication();
        sp.setPageId(pageId);
        sp.setScheduledAt(scheduledAt);
        return scheduledRepo.save(sp);
    }

    public void cancel(UUID scheduledId) {
        scheduledRepo.deleteById(scheduledId);
    }

    public List<ScheduledPublication> findByPageId(UUID pageId) {
        return scheduledRepo.findByPageId(pageId);
    }

    @Scheduled(fixedRate = 60000) // every minute
    public void executeScheduledPublications() {
        List<ScheduledPublication> pending = scheduledRepo
                .findByExecutedFalseAndScheduledAtBefore(Instant.now());

        for (ScheduledPublication sp : pending) {
            try {
                pageService.updateEstado(sp.getPageId(), EstadoConteudo.PUBLISHED);
                sp.setExecuted(true);
                sp.setExecutedAt(Instant.now());
                scheduledRepo.save(sp);
                log.info("Auto-published page {} at {}", sp.getPageId(), sp.getScheduledAt());
            } catch (Exception e) {
                log.error("Failed to auto-publish page {}: {}", sp.getPageId(), e.getMessage());
            }
        }
    }
}
