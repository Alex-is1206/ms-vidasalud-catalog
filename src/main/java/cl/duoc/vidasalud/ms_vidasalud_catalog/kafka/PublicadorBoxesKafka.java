package cl.duoc.vidasalud.ms_vidasalud_catalog.kafka;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import cl.duoc.vidasalud.ms_vidasalud_catalog.kafka.config.NombresKafka;
import cl.duoc.vidasalud.ms_vidasalud_catalog.kafka.evento.AccionBox;
import cl.duoc.vidasalud.ms_vidasalud_catalog.kafka.evento.BoxEstadoEvento;
import cl.duoc.vidasalud.ms_vidasalud_catalog.model.Box;

/**
 * Publica el estado actual de cada box en el topico compactado de streaming.
 *
 * La clave del mensaje es el boxId: Kafka conserva solo el ultimo estado de cada box,
 * y todos los cambios de un mismo box quedan en la misma particion y en orden.
 *
 * Un fallo de Kafka se registra pero no revierte el cambio, que ya quedo en Oracle.
 */
@Component
public class PublicadorBoxesKafka {

    private static final Logger log = LoggerFactory.getLogger(PublicadorBoxesKafka.class);

    private final KafkaTemplate<String, BoxEstadoEvento> kafkaTemplate;
    private final NombresKafka nombres;

    public PublicadorBoxesKafka(KafkaTemplate<String, BoxEstadoEvento> kafkaTemplate, NombresKafka nombres) {
        this.kafkaTemplate = kafkaTemplate;
        this.nombres = nombres;
    }

    /**
     * Publica la foto completa del box despues de crearlo o actualizarlo.
     */
    public void estadoBox(Box box, AccionBox accion) {
        BoxEstadoEvento evento = BoxEstadoEvento.de(
                box.getId(), box.getNombre(), box.getCapacidad(), box.getActivo(), accion);

        String topico = nombres.topicoBoxes();
        String clave = String.valueOf(evento.boxId());

        try {
            kafkaTemplate.send(topico, clave, evento).whenComplete((resultado, error) -> {
                if (error != null) {
                    log.error("kafka | no se pudo publicar | box {} {} | eventId {} | motivo {}",
                            evento.accion(), evento.boxId(), evento.eventId(), error.getMessage());
                    return;
                }
                RecordMetadata meta = resultado.getRecordMetadata();
                log.info("kafka | evento publicado | box {} {} | activo {} | topico {} | particion {} | offset {}",
                        evento.accion(), evento.boxId(), evento.activo(),
                        meta.topic(), meta.partition(), meta.offset());
            });
        } catch (Exception e) {
            log.error("kafka | no se pudo enviar | box {} {} | eventId {} | motivo {}",
                    evento.accion(), evento.boxId(), evento.eventId(), e.getMessage());
        }
    }
}
