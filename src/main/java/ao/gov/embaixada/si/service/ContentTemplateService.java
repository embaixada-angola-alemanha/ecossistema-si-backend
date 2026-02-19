package ao.gov.embaixada.si.service;

import ao.gov.embaixada.si.dto.ContentTemplateCreateRequest;
import ao.gov.embaixada.si.dto.ContentTemplateResponse;
import ao.gov.embaixada.si.entity.ContentTemplate;
import ao.gov.embaixada.si.enums.TipoPagina;
import ao.gov.embaixada.si.exception.DuplicateResourceException;
import ao.gov.embaixada.si.exception.ResourceNotFoundException;
import ao.gov.embaixada.si.repository.ContentTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ContentTemplateService {

    private final ContentTemplateRepository templateRepository;

    public ContentTemplateService(ContentTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public ContentTemplateResponse create(ContentTemplateCreateRequest request) {
        if (templateRepository.existsByNome(request.nome())) {
            throw new DuplicateResourceException("Template '" + request.nome() + "' already exists");
        }

        ContentTemplate template = new ContentTemplate();
        template.setNome(request.nome());
        template.setDescricao(request.descricao());
        template.setTipoPagina(request.tipoPagina());
        template.setTemplateHtml(request.templateHtml());
        template.setSchemaJson(request.schemaJson());

        return toResponse(templateRepository.save(template));
    }

    @Transactional(readOnly = true)
    public ContentTemplateResponse findById(UUID id) {
        return toResponse(templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + id)));
    }

    @Transactional(readOnly = true)
    public List<ContentTemplateResponse> findAll(TipoPagina tipo) {
        if (tipo != null) {
            return templateRepository.findByTipoPaginaAndActivo(tipo, true)
                    .stream().map(this::toResponse).toList();
        }
        return templateRepository.findByActivo(true)
                .stream().map(this::toResponse).toList();
    }

    public ContentTemplateResponse update(UUID id, ContentTemplateCreateRequest request) {
        ContentTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + id));

        template.setNome(request.nome());
        template.setDescricao(request.descricao());
        template.setTipoPagina(request.tipoPagina());
        template.setTemplateHtml(request.templateHtml());
        template.setSchemaJson(request.schemaJson());

        return toResponse(templateRepository.save(template));
    }

    public void delete(UUID id) {
        ContentTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + id));
        template.setActivo(false);
        templateRepository.save(template);
    }

    private ContentTemplateResponse toResponse(ContentTemplate t) {
        return new ContentTemplateResponse(
                t.getId(), t.getNome(), t.getDescricao(), t.getTipoPagina(),
                t.getTemplateHtml(), t.getSchemaJson(), t.isActivo(),
                t.getCreatedAt(), t.getUpdatedAt());
    }
}
