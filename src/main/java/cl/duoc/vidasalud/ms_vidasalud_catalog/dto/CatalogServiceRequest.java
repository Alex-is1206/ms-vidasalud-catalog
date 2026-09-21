package cl.duoc.vidasalud.ms_vidasalud_catalog.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CatalogServiceRequest(

        @NotBlank
        String nombre,

        String descripcion,

        @NotNull
        @PositiveOrZero
        BigDecimal precio,

        @NotNull
        @Positive
        Integer duracionMinutos,

        Boolean activo

) {
}