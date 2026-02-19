package ao.gov.embaixada.si.repository;

import ao.gov.embaixada.si.entity.Menu;
import ao.gov.embaixada.si.enums.LocalizacaoMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {

    Optional<Menu> findByNome(String nome);

    boolean existsByNome(String nome);

    List<Menu> findByLocalizacao(LocalizacaoMenu localizacao);

    List<Menu> findByActivo(boolean activo);

    Optional<Menu> findByLocalizacaoAndActivo(LocalizacaoMenu localizacao, boolean activo);
}
