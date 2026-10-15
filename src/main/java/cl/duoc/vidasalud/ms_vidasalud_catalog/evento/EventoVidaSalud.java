package cl.duoc.vidasalud.ms_vidasalud_catalog.evento;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Sobre comun de eventos de VidaSalud. Misma estructura que consume
 * ms-vidasalud-notify, para que productor y consumidor hablen el mismo contrato.
 */
public record EventoVidaSalud(
        String type,
        String eventId,
        String timestamp,
        String traceId,
        String correlationId,
        Map<String, Object> payload) {

    public static EventoVidaSalud de(String type, String traceId, Map<String, Object> payload) {
        String id = UUID.randomUUID().toString();
        return new EventoVidaSalud(
                type,
                id,
                Instant.now().toString(),
                traceId != null ? traceId : id,
                id,
                payload);
    }
}