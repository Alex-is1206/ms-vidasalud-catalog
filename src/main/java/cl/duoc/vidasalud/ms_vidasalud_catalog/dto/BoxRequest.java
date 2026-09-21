package cl.duoc.vidasalud.ms_vidasalud_catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BoxRequest(

        @NotBlank
        String nombre,

        @NotNull
        @Positive
        Integer capacidad,

        Boolean activo

) {
}