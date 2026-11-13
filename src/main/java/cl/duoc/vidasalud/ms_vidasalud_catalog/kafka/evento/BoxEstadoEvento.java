package cl.duoc.vidasalud.ms_vidasalud_catalog.kafka.evento;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Evento de streaming publicado en el topico compactado vidasalud.boxes.estado.
 *
 * Es una "foto" completa del box despues del cambio, no solo lo que cambio: como el
 * topico es compactado (cleanup.policy=compact), Kafka conserva el ultimo mensaje de
 * cada clave (boxId). Asi, un consumidor nuevo que lee desde el inicio reconstruye el
 * estado actual de todos los boxes sin consultar la base de datos.
 *
 * eventId    idempotencia y trazabilidad.
 * ocurridoEn cuando se registro el cambio (ISO-8601).
 * boxId      clave del mensaje: la compactacion conserva el ultimo estado por box.
 * nombre     para mostrar en el panel.
 * capacidad  metricas de capacidad.
 * activo     boxes disponibles.
 * accion     si el box se creo o se actualizo.
 * origen     microservicio que emitio el evento.
 * version    version del esquema.
 */
public record BoxEstadoEvento(
        @NotBlank String eventId,
        @NotBlank String ocurridoEn,
        @NotNull Long boxId,
        @NotBlank String nombre,
        @NotNull @PositiveOrZero Integer capacidad,
        @NotNull Boolean activo,
        @NotNull AccionBox accion,
        @NotBlank String origen,
        int version
) {

    public static final int VERSION_ACTUAL = 1;
    public static final String ORIGEN = "ms-vidasalud-catalog";

    public static BoxEstadoEvento de(Long boxId, String nombre, Integer capacidad, Boolean activo, AccionBox accion) {
        return new BoxEstadoEvento(
                UUID.randomUUID().toString(),
                Instant.now().toString(),
                boxId,
                nombre,
                capacidad,
                activo,
                accion,
                ORIGEN,
                VERSION_ACTUAL);
    }
}
