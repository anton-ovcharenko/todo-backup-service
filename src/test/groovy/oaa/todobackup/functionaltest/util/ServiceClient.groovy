package oaa.todobackup.functionaltest.util

import com.jayway.restassured.response.Response

import static com.jayway.restassured.RestAssured.when

class ServiceClient {
    def serverPort

    ServiceClient(def serverPort) {
        this.serverPort = serverPort
    }

    Response getBackups() {
        makeGetRequest("http://localhost:${serverPort}/backups")
    }

    Response startBackups() {
        makePostRequest("http://localhost:${serverPort}/backups")
    }

    Response getExport(def backupId) {
        makeGetRequest("http://localhost:${serverPort}/exports/${backupId}")
    }

    private Response makeGetRequest(String url) {
        Response response = when().get(url)
        response.prettyPrint()
        response
    }

    private Response makePostRequest(String url) {
        Response response = when().post(url)
        response.prettyPrint()
        response
    }
}
