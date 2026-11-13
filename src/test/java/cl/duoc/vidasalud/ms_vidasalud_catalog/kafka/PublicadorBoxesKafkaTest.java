package cl.duoc.vidasalud.ms_vidasalud_catalog.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import cl.duoc.vidasalud.ms_vidasalud_catalog.kafka.config.NombresKafka;
import cl.duoc.vidasalud.ms_vidasalud_catalog.kafka.evento.AccionBox;
import cl.duoc.vidasalud.ms_vidasalud_catalog.kafka.evento.BoxEstadoEvento;
import cl.duoc.vidasalud.ms_vidasalud_catalog.model.Box;

/**
 * Pruebas del productor Kafka de boxes sin broker real.
 */
@ExtendWith(MockitoExtension.class)
class PublicadorBoxesKafkaTest {

    private static final String TOPICO = "vidasalud.boxes.estado";

    @Mock
    private KafkaTemplate<String, BoxEstadoEvento> kafkaTemplate;

    private PublicadorBoxesKafka publicador;

    @BeforeEach
    void setUp() {
        NombresKafka nombres = new NombresKafka("localhost:9092", "test", "vidasalud.atenciones.eventos", TOPICO);
        publicador = new PublicadorBoxesKafka(kafkaTemplate, nombres);
    }

    @Test
    @DisplayName("Envia la foto completa del box al topico compactado con clave boxId")
    void enviaEstadoConClaveBoxId() {
        when(kafkaTemplate.send(anyString(), anyString(), any(BoxEstadoEvento.class)))
                .thenReturn(new CompletableFuture<SendResult<String, BoxEstadoEvento>>());

        publicador.estadoBox(new Box(4L, "Box 4", 2, true), AccionBox.CREADO);

        ArgumentCaptor<BoxEstadoEvento> evento = ArgumentCaptor.forClass(BoxEstadoEvento.class);
        verify(kafkaTemplate).send(eq(TOPICO), eq("4"), evento.capture());
        assertThat(evento.getValue().nombre()).isEqualTo("Box 4");
        assertThat(evento.getValue().capacidad()).isEqualTo(2);
        assertThat(evento.getValue().activo()).isTrue();
        assertThat(evento.getValue().accion()).isEqualTo(AccionBox.CREADO);
        assertThat(evento.getValue().eventId()).isNotBlank();
    }

    @Test
    @DisplayName("Si Kafka falla, el error se registra y no se propaga al negocio")
    void falloNoSePropaga() {
        when(kafkaTemplate.send(anyString(), anyString(), any(BoxEstadoEvento.class)))
                .thenThrow(new RuntimeException("cluster no disponible"));

        assertThatCode(() -> publicador.estadoBox(new Box(4L, "Box 4", 2, true), AccionBox.ACTUALIZADO))
                .doesNotThrowAnyException();
    }
}
