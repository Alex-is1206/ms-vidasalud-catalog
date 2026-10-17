package cl.duoc.vidasalud.ms_vidasalud_catalog;

import cl.duoc.vidasalud.ms_vidasalud_catalog.config.NombresRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(NombresRabbit.class)
public class MsVidasaludCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsVidasaludCatalogApplication.class, args);
    }
}