package ao.gov.embaixada.si.integration;

import ao.gov.embaixada.commons.integration.IntegrationEventPublisher;
import ao.gov.embaixada.commons.integration.event.EventTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Publishes SI events to the cross-system integration exchange.
 * Consumed by WN (event announcements) and GPJ (monitoring).
 */
@Service
public class SiEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SiEventPublisher.class);

    private final IntegrationEventPublisher publisher;

    public SiEventPublisher(@Nullable IntegrationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void pagePublished(String pageId, String slug, String tituloPt) {
        if (publisher == null) return;
        publisher.publish(EventTypes.SOURCE_SI, EventTypes.SI_PAGE_PUBLISHED, pageId, "Page",
            Map.of("slug", slug, "tituloPt", tituloPt));
    }

    public void eventPublished(String eventId, String slug, String tituloPt, String dataInicio) {
        if (publisher == null) return;
        publisher.publish(EventTypes.SOURCE_SI, EventTypes.SI_EVENT_PUBLISHED, eventId, "Event",
            Map.of("slug", slug, "tituloPt", tituloPt, "dataInicio", dataInicio));
        log.info("Published SI event: slug={}", slug);
    }

    public void eventCancelled(String eventId, String slug) {
        if (publisher == null) return;
        publisher.publish(EventTypes.SOURCE_SI, EventTypes.SI_EVENT_CANCELLED, eventId, "Event",
            Map.of("slug", slug));
    }

    public void contactUpdated(String contactId, String tipo) {
        if (publisher == null) return;
        publisher.publish(EventTypes.SOURCE_SI, EventTypes.SI_CONTACT_UPDATED, contactId, "Contact",
            Map.of("tipo", tipo));
    }
}
