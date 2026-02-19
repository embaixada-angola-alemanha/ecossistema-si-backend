package ao.gov.embaixada.si.repository;

import ao.gov.embaixada.si.entity.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContactInfoRepository extends JpaRepository<ContactInfo, UUID> {

    List<ContactInfo> findByActivoOrderBySortOrderAsc(boolean activo);
}
