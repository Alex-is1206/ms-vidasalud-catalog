package cl.duoc.vidasalud.ms_vidasalud_catalog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.vidasalud.ms_vidasalud_catalog.exception.NotFoundException;
import cl.duoc.vidasalud.ms_vidasalud_catalog.model.CatalogService;
import cl.duoc.vidasalud.ms_vidasalud_catalog.repository.CatalogServiceRepository;

@Service
public class CatalogServiceService {

    private final CatalogServiceRepository repository;

    public CatalogServiceService(CatalogServiceRepository repository) {
        this.repository = repository;
    }

    // Crear prestación
    public CatalogService crear(CatalogService servicio) {

        servicio.setId(null);

        if (servicio.getActivo() == null) {
            servicio.setActivo(true);
        }

        return repository.save(servicio);
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

        return repository.save(existente);
    }
}