package cl.duoc.vidasalud.ms_vidasalud_catalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.vidasalud.ms_vidasalud_catalog.exception.NotFoundException;
import cl.duoc.vidasalud.ms_vidasalud_catalog.kafka.PublicadorBoxesKafka;
import cl.duoc.vidasalud.ms_vidasalud_catalog.kafka.evento.AccionBox;
import cl.duoc.vidasalud.ms_vidasalud_catalog.mensajeria.PublicadorCatalogo;
import cl.duoc.vidasalud.ms_vidasalud_catalog.model.Box;
import cl.duoc.vidasalud.ms_vidasalud_catalog.repository.BoxRepository;

/**
 * Pruebas de la lógica de boxes, sin base de datos ni brokers.
 * Los publicadores de RabbitMQ y Kafka se simulan: la prueba verifica que el
 * servicio los llama, no que los brokers reciban el mensaje.
 */
@ExtendWith(MockitoExtension.class)
class BoxServiceTest {

    @Mock
    private BoxRepository repository;

    @Mock
    private PublicadorCatalogo publicador;

    @Mock
    private PublicadorBoxesKafka publicadorKafka;

    @InjectMocks
    private BoxService service;

    @Test
    @DisplayName("Un box creado sin estado queda activo y se publica en RabbitMQ y en Kafka")
    void crearSinEstadoQuedaActivo() {
        when(repository.save(any(Box.class))).thenAnswer(inv -> inv.getArgument(0));

        Box box = new Box();
        box.setNombre("Box 1");
        box.setCapacidad(1);

        Box creado = service.crear(box);

        assertThat(creado.getActivo()).isTrue();
        verify(publicador).cambioRegistrado(any(), any(), any(), any());
        verify(publicadorKafka).estadoBox(creado, AccionBox.CREADO);
    }

    @Test
    @DisplayName("Actualizar un box publica su nuevo estado en Kafka como ACTUALIZADO")
    void actualizarPublicaEstado() {
        Box existente = new Box(5L, "Box 5", 2, true);
        when(repository.findById(5L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Box.class))).thenAnswer(inv -> inv.getArgument(0));

        Box resultado = service.actualizar(5L, new Box(null, "Box 5", 2, false));

        assertThat(resultado.getActivo()).isFalse();
        verify(publicadorKafka).estadoBox(resultado, AccionBox.ACTUALIZADO);
    }

    @Test
    @DisplayName("Box inexistente: NotFoundException (404) y no se publica nada")
    void boxInexistente() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(999L))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(publicador, publicadorKafka);
    }
}
