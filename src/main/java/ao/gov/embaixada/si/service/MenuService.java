package ao.gov.embaixada.si.service;

import ao.gov.embaixada.si.dto.*;
import ao.gov.embaixada.si.entity.Menu;
import ao.gov.embaixada.si.entity.MenuItem;
import ao.gov.embaixada.si.enums.LocalizacaoMenu;
import ao.gov.embaixada.si.exception.DuplicateResourceException;
import ao.gov.embaixada.si.exception.ResourceNotFoundException;
import ao.gov.embaixada.si.repository.MenuItemRepository;
import ao.gov.embaixada.si.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuRepository menuRepository, MenuItemRepository menuItemRepository) {
        this.menuRepository = menuRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public MenuResponse create(MenuCreateRequest request) {
        if (menuRepository.existsByNome(request.nome())) {
            throw new DuplicateResourceException("Menu '" + request.nome() + "' already exists");
        }

        Menu menu = new Menu();
        menu.setNome(request.nome());
        menu.setLocalizacao(request.localizacao());
        menu.setActivo(true);

        return toResponse(menuRepository.save(menu));
    }

    @Transactional(readOnly = true)
    public MenuResponse findById(UUID id) {
        return toResponse(menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found: " + id)));
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> findAll() {
        return menuRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MenuResponse findByLocalizacao(LocalizacaoMenu localizacao) {
        return toResponse(menuRepository.findByLocalizacaoAndActivo(localizacao, true)
                .orElseThrow(() -> new ResourceNotFoundException("No active menu for: " + localizacao)));
    }

    public MenuItemResponse addItem(UUID menuId, MenuItemCreateRequest request) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found: " + menuId));

        MenuItem item = new MenuItem();
        item.setMenu(menu);
        item.setLabelPt(request.labelPt());
        item.setLabelEn(request.labelEn());
        item.setLabelDe(request.labelDe());
        item.setLabelCs(request.labelCs());
        item.setUrl(request.url());
        item.setPageId(request.pageId());
        item.setParentId(request.parentId());
        item.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
        item.setOpenNewTab(request.openNewTab());
        item.setIcon(request.icon());

        return toItemResponse(menuItemRepository.save(item));
    }

    public void removeItem(UUID menuId, UUID itemId) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + itemId));
        if (!item.getMenu().getId().equals(menuId)) {
            throw new ResourceNotFoundException("Menu item does not belong to this menu");
        }
        menuItemRepository.delete(item);
    }

    public MenuResponse toggleActive(UUID id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found: " + id));
        menu.setActivo(!menu.isActivo());
        return toResponse(menuRepository.save(menu));
    }

    public void delete(UUID id) {
        if (!menuRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu not found: " + id);
        }
        menuRepository.deleteById(id);
    }

    private MenuResponse toResponse(Menu menu) {
        List<MenuItemResponse> items = menu.getItems().stream()
                .map(this::toItemResponse).toList();
        return new MenuResponse(menu.getId(), menu.getNome(), menu.getLocalizacao(),
                menu.isActivo(), items, menu.getCreatedAt(), menu.getUpdatedAt());
    }

    private MenuItemResponse toItemResponse(MenuItem item) {
        return new MenuItemResponse(item.getId(), item.getLabelPt(), item.getLabelEn(),
                item.getLabelDe(), item.getLabelCs(), item.getUrl(), item.getPageId(),
                item.getParentId(), item.getSortOrder(), item.isOpenNewTab(),
                item.getIcon(), item.isActivo());
    }
}
