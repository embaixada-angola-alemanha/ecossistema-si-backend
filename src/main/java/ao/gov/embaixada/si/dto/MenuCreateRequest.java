package ao.gov.embaixada.si.dto;

import ao.gov.embaixada.si.enums.LocalizacaoMenu;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuCreateRequest(
        @NotBlank String nome,
        @NotNull LocalizacaoMenu localizacao
) {}
