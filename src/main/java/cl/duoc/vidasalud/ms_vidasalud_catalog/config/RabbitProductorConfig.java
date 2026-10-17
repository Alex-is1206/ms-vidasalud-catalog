package cl.duoc.vidasalud.ms_vidasalud_catalog.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion del lado productor del catalogo.
 *
 * Usa un exchange de tipo direct cuyo nombre se lee desde NombresRabbit
 * porque sus eventos tienen un unico destino conocido: la cola de auditoria.
 */
@Configuration
public class RabbitProductorConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitProductorConfig.class);

    public static final String EXCHANGE_DIRECT = "cmd.direct";
    public static final String RK_CATALOGO_CAMBIO = "catalogo.cambio";

    private final NombresRabbit nombres;

    public RabbitProductorConfig(NombresRabbit nombres) {
        this.nombres = nombres;
    }

    @Bean
    DirectExchange exchangeCatalogo() {
        String nombreExchange = (nombres != null && nombres.exchangeDirect() != null)
                ? nombres.exchangeDirect()
                : EXCHANGE_DIRECT;
        return new DirectExchange(nombreExchange, true, false);
    }

    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    MessageConverter conversorJson() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter conversorJson) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(conversorJson);
        template.setMandatory(true);

        template.setConfirmCallback((correlacion, aceptado, motivo) -> {
            if (!aceptado) {
                log.error("el broker rechazo el mensaje {}: {}",
                        correlacion != null ? correlacion.getId() : "sin-id", motivo);
            }
        });

        template.setReturnsCallback(devuelto -> log.error(
                "mensaje sin cola destino | exchange {} | routing key {} | motivo {}",
                devuelto.getExchange(), devuelto.getRoutingKey(), devuelto.getReplyText()));

        return template;
    }
}