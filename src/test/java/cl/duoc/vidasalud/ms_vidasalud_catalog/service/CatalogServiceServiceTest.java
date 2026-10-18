package cl.duoc.vidasalud.ms_vidasalud_catalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.vidasalud.ms_vidasalud_catalog.exception.NotFoundException;
import cl.duoc.vidasalud.ms_vidasalud_catalog.mensajeria.PublicadorCatalogo;
import cl.duoc.vidasalud.ms_vidasalud_catalog.model.CatalogService;
import cl.duoc.vidasalud.ms_vidasalud_catalog.repository.CatalogServiceRepository;

/**
 * Pruebas de la lógica de prestaciones, sin base de datos ni broker.
 * El publicador se simula: la prueba verifica que el servicio lo llama,
 * no que RabbitMQ reciba el mensaje.
 */
@ExtendWith(MockitoExtension.class)
class CatalogServiceServiceTest {

    @Mock
    private CatalogServiceRepository repository;

    @Mock
    private PublicadorCatalogo publicador;

    @InjectMocks
    private CatalogServiceService service;

    private CatalogService prestacion(String nombre, BigDecimal precio, Boolean activo) {
        CatalogService p = new CatalogService();
        p.setNombre(nombre);
        p.setDescripcion("Prueba");
        p.setPrecio(precio);
        p.setDuracionMinutos(30);
        p.setActivo(activo);
        return p;
    }

    @Test
    @DisplayName("Una prestación creada sin estado queda activa y se publica el cambio")
    void crearSinEstadoQuedaActiva() {
        when(repository.save(any(CatalogService.class))).thenAnswer(inv -> inv.getArgument(0));

        CatalogService creada = service.crear(prestacion("Consulta", BigDecimal.TEN, null));

        assertThat(creada.getActivo()).isTrue();
        assertThat(creada.getId()).isNull();
        verify(publicador).cambioRegistrado(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Actualizar cambia precio y estado de la prestación existente y se publica el cambio")
    void actualizarPrestacionExistente() {
        CatalogService existente = prestacion("Consulta", BigDecimal.TEN, true);
        existente.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(CatalogService.class))).thenAnswer(inv -> inv.getArgument(0));

        CatalogService resultado = service.actualizar(1L,
                prestacion("Consulta", new BigDecimal("30000"), false));

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getPrecio()).isEqualByComparingTo("30000");
        assertThat(resultado.getActivo()).isFalse();
        verify(publicador).cambioRegistrado(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Prestación inexistente: NotFoundException (404) y no se publica nada")
    void prestacionInexistente() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("999");
        verifyNoInteractions(publicador);
    }
}