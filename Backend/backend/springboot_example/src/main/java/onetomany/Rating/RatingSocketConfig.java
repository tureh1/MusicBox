package onetomany.Rating;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class RatingSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter1() {
        return new ServerEndpointExporter();
    }

}

