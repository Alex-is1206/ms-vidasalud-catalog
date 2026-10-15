package cl.duoc.vidasalud.ms_vidasalud_catalog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.vidasalud.ms_vidasalud_catalog.exception.NotFoundException;
import cl.duoc.vidasalud.ms_vidasalud_catalog.mensajeria.PublicadorCatalogo;
import cl.duoc.vidasalud.ms_vidasalud_catalog.model.CatalogService;
import cl.duoc.vidasalud.ms_vidasalud_catalog.repository.CatalogServiceRepository;

@Service
public class CatalogServiceService {

    private final CatalogServiceRepository repository;
    private final PublicadorCatalogo publicador;

    public CatalogServiceService(CatalogServiceRepository repository,
                                 PublicadorCatalogo publicador) {
        this.repository = repository;
        this.publicador = publicador;
    }

    // Crear prestación
    public CatalogService crear(CatalogService servicio) {

        servicio.setId(null);

        if (servicio.getActivo() == null) {
            servicio.setActivo(true);
        }

        CatalogService guardada = repository.save(servicio);

        publicador.cambioRegistrado("prestacion", "CREADO", guardada.getId(), "sistema");

        return guardada;
    }

    // Listar prestaciones
    public List<CatalogService> listar() {
        return repository.findAll();
    }

    // Buscar prestación por ID
    public CatalogService buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Prestación no encontrada con ID: " + id
                        )
                );
    }

    // Actualizar prestación
    public CatalogService actualizar(
            Long id,
            CatalogService datosActualizados) {

        CatalogService existente = buscarPorId(id);

        existente.setNombre(datosActualizados.getNombre());
        existente.setDescripcion(datosActualizados.getDescripcion());
        existente.setPrecio(datosActualizados.getPrecio());
        existente.setDuracionMinutos(
                datosActualizados.getDuracionMinutos()
        );
        existente.setActivo(datosActualizados.getActivo());

        CatalogService guardada = repository.save(existente);

        publicador.cambioRegistrado("prestacion", "ACTUALIZADO", guardada.getId(), "sistema");

        return guardada;
    }
}