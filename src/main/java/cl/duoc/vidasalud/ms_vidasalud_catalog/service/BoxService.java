package cl.duoc.vidasalud.ms_vidasalud_catalog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.vidasalud.ms_vidasalud_catalog.model.Box;
import cl.duoc.vidasalud.ms_vidasalud_catalog.repository.BoxRepository;

@Service
public class BoxService {

    private final BoxRepository repository;

    public BoxService(BoxRepository repository) {
        this.repository = repository;
    }

    public Box crear(Box box) {

        box.setId(null);

        if (box.getActivo() == null) {
            box.setActivo(true);
        }

        return repository.save(box);
    }

    public List<Box> listar() {
        return repository.findAll();
    }

    public Box buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Box no encontrado con ID: " + id
                        )
                );
    }

    public Box actualizar(Long id, Box datosActualizados) {

        Box existente = buscarPorId(id);

        existente.setNombre(datosActualizados.getNombre());
        existente.setCapacidad(datosActualizados.getCapacidad());
        existente.setActivo(datosActualizados.getActivo());

        return repository.save(existente);
    }
}