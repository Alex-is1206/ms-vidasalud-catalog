package cl.duoc.vidasalud.ms_vidasalud_catalog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.vidasalud.ms_vidasalud_catalog.exception.NotFoundException;
import cl.duoc.vidasalud.ms_vidasalud_catalog.kafka.PublicadorBoxesKafka;
import cl.duoc.vidasalud.ms_vidasalud_catalog.kafka.evento.AccionBox;
import cl.duoc.vidasalud.ms_vidasalud_catalog.mensajeria.PublicadorCatalogo;
import cl.duoc.vidasalud.ms_vidasalud_catalog.model.Box;
import cl.duoc.vidasalud.ms_vidasalud_catalog.repository.BoxRepository;

/**
 * Logica de negocio de los boxes.
 *
 * Despues de guardar delega en dos publicadores sin conocer la mensajeria:
 * - PublicadorCatalogo (RabbitMQ): registra el cambio para la cola de auditoria.
 * - PublicadorBoxesKafka (Kafka): publica el estado actual del box en el topico compactado.
 */
@Service
public class BoxService {

    private final BoxRepository repository;
    private final PublicadorCatalogo publicador;
    private final PublicadorBoxesKafka publicadorKafka;

    public BoxService(BoxRepository repository,
                      PublicadorCatalogo publicador,
                      PublicadorBoxesKafka publicadorKafka) {
        this.repository = repository;
        this.publicador = publicador;
        this.publicadorKafka = publicadorKafka;
    }

    public Box crear(Box box) {

        box.setId(null);

        if (box.getActivo() == null) {
            box.setActivo(true);
        }

        Box guardado = repository.save(box);

        // Los eventos salen solo despues de que el box quedo en la base de datos
        publicador.cambioRegistrado("box", "CREADO", guardado.getId(), "sistema");
        publicadorKafka.estadoBox(guardado, AccionBox.CREADO);

        return guardado;
    }

    public List<Box> listar() {
        return repository.findAll();
    }

    public Box buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Box no encontrado con ID: " + id
                        )
                );
    }

    public Box actualizar(Long id, Box datosActualizados) {

        Box existente = buscarPorId(id);

        existente.setNombre(datosActualizados.getNombre());
        existente.setCapacidad(datosActualizados.getCapacidad());
        existente.setActivo(datosActualizados.getActivo());

        Box guardado = repository.save(existente);

        publicador.cambioRegistrado("box", "ACTUALIZADO", guardado.getId(), "sistema");
        publicadorKafka.estadoBox(guardado, AccionBox.ACTUALIZADO);

        return guardado;
    }
}
