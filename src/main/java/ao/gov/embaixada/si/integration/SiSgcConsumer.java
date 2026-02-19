package ao.gov.embaixada.si.integration;

import ao.gov.embaixada.commons.integration.event.Exchanges;
import ao.gov.embaixada.commons.integration.event.IntegrationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes SGC events for the institutional site.
 * SI↔SGC: Consular data feed — when SGC has consular activity,
 * SI can display relevant public information (e.g. service statistics,
 * new consular services, appointment availability).
 */
@Component
public class SiSgcConsumer {

    private static final Logger log = LoggerFactory.getLogger(SiSgcConsumer.class);

    @RabbitListener(queues = Exchanges.QUEUE_SI_FROM_SGC, concurrency = "1-3")
    public void handleSgcEvent(IntegrationEvent event) {
        log.info("SI received SGC event: type={}, entity={}, entityId={}",
            event.eventType(), event.entityType(), event.entityId());

        switch (event.eventType()) {
            case "SGC_ACTIVITY_COMPLETED" -> handleActivityCompleted(event);
            case "SGC_CIDADAO_CREATED" -> handleCidadaoCreated(event);
            case "SGC_AGENDAMENTO_CREATED" -> handleAgendamentoCreated(event);
            default -> log.debug("SI ignoring SGC event type: {}", event.eventType());
        }
    }

    /**
     * When SGC completes a consular activity, SI can update public statistics
     * or highlight the service on the institutional site.
     */
    private void handleActivityCompleted(IntegrationEvent event) {
        String description = (String) event.payload().getOrDefault("description", "");
        log.info("SGC activity completed: {} — SI can update public consular info", description);
        // TODO: Update cached consular statistics or public service announcements
    }

    /**
     * Track citizen registration numbers for public dashboard.
     */
    private void handleCidadaoCreated(IntegrationEvent event) {
        log.info("New citizen registered in SGC: {} — SI updates registration stats", event.entityId());
        // TODO: Increment public citizen registration counter
    }

    /**
     * Track appointment creation for service availability info.
     */
    private void handleAgendamentoCreated(IntegrationEvent event) {
        String tipo = (String) event.payload().getOrDefault("tipo", "");
        log.info("New SGC appointment: type={} — SI can show service demand", tipo);
        // TODO: Update public service demand/availability metrics
    }
}
