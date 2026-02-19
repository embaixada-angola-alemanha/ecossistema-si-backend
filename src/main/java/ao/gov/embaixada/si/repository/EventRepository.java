package ao.gov.embaixada.si.repository;

import ao.gov.embaixada.si.entity.Event;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    Page<Event> findByEstadoOrderByDataInicioDesc(EstadoConteudo estado, Pageable pageable);

    List<Event> findByEstadoAndDataInicioAfterOrderByDataInicioAsc(EstadoConteudo estado, Instant after);

    List<Event> findByEstadoAndDataInicioBetweenOrderByDataInicioAsc(
            EstadoConteudo estado, Instant start, Instant end);

    Page<Event> findByTipoEventoAndEstadoOrderByDataInicioDesc(
            String tipoEvento, EstadoConteudo estado, Pageable pageable);
}
