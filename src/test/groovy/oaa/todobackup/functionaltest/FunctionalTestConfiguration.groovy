package oaa.todobackup.functionaltest

import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import oaa.todobackup.functionaltest.util.ServiceClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
class FunctionalTestConfiguration {

    @Value('${wiremock.port}')
    int wireMockPort
    @Value('${server.port}')
    int servicePort

    @Bean
    WireMockConfiguration wireMockConfiguration() {
        WireMockConfiguration.wireMockConfig().port(wireMockPort)
    }

    @Bean
    ServiceClient serviceClient() {
        return new ServiceClient(servicePort)
    }
}
