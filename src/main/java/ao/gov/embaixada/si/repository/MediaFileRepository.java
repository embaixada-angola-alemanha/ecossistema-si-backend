package ao.gov.embaixada.si.repository;

import ao.gov.embaixada.si.entity.MediaFile;
import ao.gov.embaixada.si.enums.TipoMedia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MediaFileRepository extends JpaRepository<MediaFile, UUID> {

    Page<MediaFile> findByTipo(TipoMedia tipo, Pageable pageable);

    Page<MediaFile> findByMimeTypeContaining(String mimeType, Pageable pageable);
}
