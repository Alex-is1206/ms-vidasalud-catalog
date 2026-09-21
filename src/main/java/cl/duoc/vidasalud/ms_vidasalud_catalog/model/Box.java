package cl.duoc.vidasalud.ms_vidasalud_catalog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CATALOG_BOXES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Box {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "catalog_box_seq"
    )
    @SequenceGenerator(
        name = "catalog_box_seq",
        sequenceName = "CATALOG_BOX_SEQ",
        allocationSize = 1
    )
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "CAPACIDAD", nullable = false)
    private Integer capacidad;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;
}