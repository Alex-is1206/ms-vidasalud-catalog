package cl.duoc.vidasalud.ms_vidasalud_catalog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.vidasalud.ms_vidasalud_catalog.dto.CatalogServiceRequest;
import cl.duoc.vidasalud.ms_vidasalud_catalog.model.CatalogService;
import cl.duoc.vidasalud.ms_vidasalud_catalog.service.CatalogServiceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/catalog/services")
public class CatalogController {

    private final CatalogServiceService service;

    public CatalogController(CatalogServiceService service) {
        this.service = service;
    }

    // Crear prestación
    @PostMapping
    public ResponseEntity<CatalogService> crear(
            @Valid @RequestBody CatalogServiceRequest request) {

        CatalogService servicio = new CatalogService();

        servicio.setNombre(request.nombre());
        servicio.setDescripcion(request.descripcion());
        servicio.setPrecio(request.precio());
        servicio.setDuracionMinutos(request.duracionMinutos());
        servicio.setActivo(request.activo());

        CatalogService creado = service.crear(servicio);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creado);
    }

    // Listar prestaciones
    @GetMapping
    public List<CatalogService> listar() {
        return service.listar();
    }

    // Actualizar prestación
    @PutMapping("/{id}")
    public CatalogService actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CatalogServiceRequest request) {

        CatalogService existente = service.buscarPorId(id);

        CatalogService datosActualizados = new CatalogService();

        datosActualizados.setNombre(request.nombre());
        datosActualizados.setDescripcion(request.descripcion());
        datosActualizados.setPrecio(request.precio());
        datosActualizados.setDuracionMinutos(request.duracionMinutos());

        if (request.activo() != null) {
            datosActualizados.setActivo(request.activo());
        } else {
            datosActualizados.setActivo(existente.getActivo());
        }

        return service.actualizar(id, datosActualizados);
    }
}