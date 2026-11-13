package cl.duoc.vidasalud.ms_vidasalud_catalog;

import cl.duoc.vidasalud.ms_vidasalud_catalog.config.NombresRabbit;
import cl.duoc.vidasalud.ms_vidasalud_catalog.kafka.config.NombresKafka;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({NombresRabbit.class, NombresKafka.class})
public class MsVidasaludCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsVidasaludCatalogApplication.class, args);
    }
}
