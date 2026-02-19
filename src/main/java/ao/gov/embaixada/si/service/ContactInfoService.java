package ao.gov.embaixada.si.service;

import ao.gov.embaixada.si.dto.ContactInfoCreateRequest;
import ao.gov.embaixada.si.dto.ContactInfoResponse;
import ao.gov.embaixada.si.entity.ContactInfo;
import ao.gov.embaixada.si.exception.ResourceNotFoundException;
import ao.gov.embaixada.si.repository.ContactInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ContactInfoService {

    private final ContactInfoRepository contactInfoRepository;

    public ContactInfoService(ContactInfoRepository contactInfoRepository) {
        this.contactInfoRepository = contactInfoRepository;
    }

    public ContactInfoResponse create(ContactInfoCreateRequest request) {
        ContactInfo contact = new ContactInfo();
        mapRequestToEntity(request, contact);
        return toResponse(contactInfoRepository.save(contact));
    }

    @Transactional(readOnly = true)
    public ContactInfoResponse findById(UUID id) {
        return toResponse(contactInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact info not found: " + id)));
    }

    @Transactional(readOnly = true)
    public List<ContactInfoResponse> findAll() {
        return contactInfoRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ContactInfoResponse> findActive() {
        return contactInfoRepository.findByActivoOrderBySortOrderAsc(true)
                .stream().map(this::toResponse).toList();
    }

    public ContactInfoResponse update(UUID id, ContactInfoCreateRequest request) {
        ContactInfo contact = contactInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact info not found: " + id));
        mapRequestToEntity(request, contact);
        return toResponse(contactInfoRepository.save(contact));
    }

    public ContactInfoResponse toggleActive(UUID id) {
        ContactInfo contact = contactInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact info not found: " + id));
        contact.setActivo(!contact.isActivo());
        return toResponse(contactInfoRepository.save(contact));
    }

    public void delete(UUID id) {
        if (!contactInfoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contact info not found: " + id);
        }
        contactInfoRepository.deleteById(id);
    }

    private void mapRequestToEntity(ContactInfoCreateRequest request, ContactInfo contact) {
        contact.setDepartamento(request.departamento());
        contact.setEndereco(request.endereco());
        contact.setCidade(request.cidade());
        contact.setCodigoPostal(request.codigoPostal());
        contact.setPais(request.pais());
        contact.setTelefone(request.telefone());
        contact.setFax(request.fax());
        contact.setEmail(request.email());
        contact.setHorarioPt(request.horarioPt());
        contact.setHorarioEn(request.horarioEn());
        contact.setHorarioDe(request.horarioDe());
        contact.setLatitude(request.latitude());
        contact.setLongitude(request.longitude());
        contact.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
    }

    private ContactInfoResponse toResponse(ContactInfo contact) {
        return new ContactInfoResponse(
                contact.getId(),
                contact.getDepartamento(),
                contact.getEndereco(),
                contact.getCidade(),
                contact.getCodigoPostal(),
                contact.getPais(),
                contact.getTelefone(),
                contact.getFax(),
                contact.getEmail(),
                contact.getHorarioPt(),
                contact.getHorarioEn(),
                contact.getHorarioDe(),
                contact.getLatitude(),
                contact.getLongitude(),
                contact.getSortOrder(),
                contact.isActivo(),
                contact.getCreatedAt(),
                contact.getUpdatedAt());
    }
}
