package cl.duoc.vidasalud.ms_vidasalud_catalog.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CATALOG_SERVICES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogService {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "catalog_service_seq"
    )
    @SequenceGenerator(
        name = "catalog_service_seq",
        sequenceName = "CATALOG_SERVICE_SEQ",
        allocationSize = 1
    )
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 255)
    private String descripcion;

    @Column(name = "PRECIO", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(name = "DURACION_MINUTOS", nullable = false)
    private Integer duracionMinutos;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;
}