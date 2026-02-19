package ao.gov.embaixada.si.service;

import ao.gov.embaixada.commons.storage.StorageService;
import ao.gov.embaixada.si.dto.MediaFileResponse;
import ao.gov.embaixada.si.entity.MediaFile;
import ao.gov.embaixada.si.enums.TipoMedia;
import ao.gov.embaixada.si.exception.ResourceNotFoundException;
import ao.gov.embaixada.si.repository.MediaFileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@Transactional
public class MediaService {

    private final MediaFileRepository mediaFileRepository;
    private final StorageService storageService;

    public MediaService(MediaFileRepository mediaFileRepository, StorageService storageService) {
        this.mediaFileRepository = mediaFileRepository;
        this.storageService = storageService;
    }

    public MediaFileResponse upload(MultipartFile file, String altPt, String altEn, String altDe, String altCs) {
        String originalName = file.getOriginalFilename();
        String extension = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String fileName = UUID.randomUUID() + extension;
        String objectKey = "media/" + fileName;

        try {
            storageService.upload(storageService.getDefaultBucket(),
                    objectKey, file.getInputStream(), file.getSize(), file.getContentType());
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }

        MediaFile media = new MediaFile();
        media.setFileName(fileName);
        media.setOriginalName(originalName != null ? originalName : fileName);
        media.setMimeType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        media.setTipo(resolveType(file.getContentType()));
        media.setSize(file.getSize());
        media.setBucket(storageService.getDefaultBucket());
        media.setObjectKey(objectKey);
        media.setAltPt(altPt);
        media.setAltEn(altEn);
        media.setAltDe(altDe);
        media.setAltCs(altCs);

        return toResponse(mediaFileRepository.save(media));
    }

    @Transactional(readOnly = true)
    public MediaFileResponse findById(UUID id) {
        return toResponse(mediaFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found: " + id)));
    }

    @Transactional(readOnly = true)
    public Page<MediaFileResponse> findAll(TipoMedia tipo, Pageable pageable) {
        if (tipo != null) {
            return mediaFileRepository.findByTipo(tipo, pageable).map(this::toResponse);
        }
        return mediaFileRepository.findAll(pageable).map(this::toResponse);
    }

    public InputStream download(UUID id) {
        MediaFile media = mediaFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found: " + id));
        return storageService.download(media.getBucket(), media.getObjectKey());
    }

    public void delete(UUID id) {
        MediaFile media = mediaFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found: " + id));
        storageService.delete(media.getBucket(), media.getObjectKey());
        mediaFileRepository.delete(media);
    }

    private TipoMedia resolveType(String mimeType) {
        if (mimeType == null) return TipoMedia.DOCUMENT;
        if (mimeType.startsWith("image/")) return TipoMedia.IMAGE;
        if (mimeType.startsWith("video/")) return TipoMedia.VIDEO;
        if (mimeType.startsWith("audio/")) return TipoMedia.AUDIO;
        return TipoMedia.DOCUMENT;
    }

    private MediaFileResponse toResponse(MediaFile media) {
        return new MediaFileResponse(
                media.getId(), media.getFileName(), media.getOriginalName(),
                media.getMimeType(), media.getTipo(), media.getSize(),
                media.getAltPt(), media.getAltEn(), media.getAltDe(), media.getAltCs(),
                media.getWidth(), media.getHeight(), media.getCreatedAt());
    }
}
