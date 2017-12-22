package com.puzzleitc.jenkins

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

/**
 * Docker Hub API utility class.
 */
class DockerHub {
    static String BASE_URL = "https://hub.docker.com/v2/"
    static String LOGIN_URL = BASE_URL + "users/login/"

    /**
     * Does a login into the Docker Hub API and returns the resulting token.
     * Returning null means no successful login.
     *
     * @return the Docker Hub API login token
     */
    @com.cloudbees.groovy.cps.NonCPS
    static String createLoginToken(String dockerHubUser, String dockerHubPwd) {
        String auth = JsonOutput.toJson([username: dockerHubUser, password: dockerHubPwd])

        URL loginUrl = new URL(DockerHub.LOGIN_URL);
        HttpURLConnection con = (HttpURLConnection) loginUrl.openConnection()
        con.setDoOutput(true)
        con.setDoInput(true)
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        con.setRequestProperty("Accept", "application/json")
        con.setRequestMethod("POST")

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream())
        wr.write(auth.toString())
        wr.flush()

        int httpResult = con.getResponseCode()
        if (httpResult == HttpURLConnection.HTTP_OK) {
            def responseContent = new JsonSlurper().parseText(con.content.text)
            return responseContent.token
        } else {
            println "HTTP response code: " + httpResult
            println "HTTP response message: " + con.getResponseMessage()
            return null
        }
    }
}