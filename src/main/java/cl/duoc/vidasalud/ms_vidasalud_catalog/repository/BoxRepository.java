package cl.duoc.vidasalud.ms_vidasalud_catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.vidasalud.ms_vidasalud_catalog.model.Box;

public interface BoxRepository
        extends JpaRepository<Box, Long> {
}