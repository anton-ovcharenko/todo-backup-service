package oaa.todobackup.functionaltest.test

import oaa.todobackup.functionaltest.FunctionalTestSpecification
import oaa.todobackup.functionaltest.util.TodoItemServerApiStub
import org.springframework.http.HttpStatus

import static org.hamcrest.Matchers.*

class MainFlowTest extends FunctionalTestSpecification {

    def "main backup flow"() {
        when: 'initially get list of backups'
        def response = serviceClient.getBackups()

        then: 'list should be empty'
        response.then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("isEmpty()", is(true))

        when: 'start new backup'
        TodoItemServerApiStub.stubGetUsers()
        response = serviceClient.startBackups()

        then: 'response should contains new backupId'
        response.then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(containsString("backupId"))

        def backupId = response.then().extract().path("backupId")

        when: 'get list of backups again until status will not be OK'
        conditions.eventually {
            serviceClient.getBackups()
                    .then().assertThat()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", is(1))
                    .body("backupId[0]", equalTo(backupId))
                    .body("status[0]", equalTo("OK"))
        }

        then:
        noExceptionThrown()

        when: 'get export'
        response = serviceClient.getExport(backupId)

        then: 'export should be correct'
        response.then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
        response.then().extract().body().asString() == export()
    }

    private static String export() {
        TodoItemServerApiStub.class
                .getClassLoader()
                .getResourceAsStream("export.csv")
                .getText("UTF-8")
    }
}
