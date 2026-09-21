package cl.duoc.vidasalud.ms_vidasalud_catalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import cl.duoc.vidasalud.ms_vidasalud_catalog.model.CatalogService;
import cl.duoc.vidasalud.ms_vidasalud_catalog.repository.CatalogServiceRepository;

/** Pruebas de la lógica de prestaciones, sin base de datos. */
@ExtendWith(MockitoExtension.class)
class CatalogServiceServiceTest {

    @Mock
    private CatalogServiceRepository repository;

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
    @DisplayName("Una prestación creada sin estado queda activa")
    void crearSinEstadoQuedaActiva() {
        when(repository.save(any(CatalogService.class))).thenAnswer(inv -> inv.getArgument(0));

        CatalogService creada = service.crear(prestacion("Consulta", BigDecimal.TEN, null));

        assertThat(creada.getActivo()).isTrue();
        assertThat(creada.getId()).isNull();
    }

    @Test
    @DisplayName("Actualizar cambia precio y estado de la prestación existente")
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
    }

    @Test
    @DisplayName("Prestación inexistente: NotFoundException (404)")
    void prestacionInexistente() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("999");
    }
}
