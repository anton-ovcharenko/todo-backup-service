package oaa.todobackup.functionaltest.util

import groovy.text.SimpleTemplateEngine
import groovy.text.Template

import static com.github.tomakehurst.wiremock.client.WireMock.*

class TodoItemServerApiStub {

    def static stubGetUsers() {
        def responseTemplate = responseTemplate("users.json")
        stubFor(get(urlPathEqualTo("/users"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(responseTemplate.make() as String)))
    }

    private static Template responseTemplate(String fileName) {
        def response = TodoItemServerApiStub.class.getClassLoader().getResourceAsStream(fileName).text
        new SimpleTemplateEngine().createTemplate(response)
    }
}
