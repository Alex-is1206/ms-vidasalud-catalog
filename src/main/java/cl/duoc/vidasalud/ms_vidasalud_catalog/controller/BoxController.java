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

import cl.duoc.vidasalud.ms_vidasalud_catalog.dto.BoxRequest;
import cl.duoc.vidasalud.ms_vidasalud_catalog.model.Box;
import cl.duoc.vidasalud.ms_vidasalud_catalog.service.BoxService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/catalog/boxes")
public class BoxController {

    private final BoxService service;

    public BoxController(BoxService service) {
        this.service = service;
    }

    // Crear box
    @PostMapping
    public ResponseEntity<Box> crear(
            @Valid @RequestBody BoxRequest request) {

        Box box = new Box();

        box.setNombre(request.nombre());
        box.setCapacidad(request.capacidad());
        box.setActivo(request.activo());

        Box creado = service.crear(box);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creado);
    }

    // Listar boxes
    @GetMapping
    public List<Box> listar() {
        return service.listar();
    }

    // Actualizar box
    @PutMapping("/{id}")
    public Box actualizar(
            @PathVariable Long id,
            @Valid @RequestBody BoxRequest request) {

        Box existente = service.buscarPorId(id);

        Box datosActualizados = new Box();

        datosActualizados.setNombre(request.nombre());
        datosActualizados.setCapacidad(request.capacidad());

        if (request.activo() != null) {
            datosActualizados.setActivo(request.activo());
        } else {
            datosActualizados.setActivo(existente.getActivo());
        }

        return service.actualizar(id, datosActualizados);
    }
}