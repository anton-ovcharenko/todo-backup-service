package oaa.todobackup.functionaltest

import oaa.todobackup.TodoBackupApplication
import oaa.todobackup.functionaltest.util.ServiceClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import static com.github.tomakehurst.wiremock.client.WireMock.reset

@ContextConfiguration(classes = [TodoBackupApplication, FunctionalTestConfiguration])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWireMock
class FunctionalTestSpecification extends Specification {

    @Autowired
    ServiceClient serviceClient

    PollingConditions conditions = new PollingConditions(timeout: 10, delay: 1, initialDelay: 0)

    def setup() {
        reset()
    }
}

