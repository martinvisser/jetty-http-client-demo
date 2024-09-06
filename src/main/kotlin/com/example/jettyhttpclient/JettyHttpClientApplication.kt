package com.example.jettyhttpclient

import org.eclipse.jetty.client.HttpClient
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.http.client.JettyClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.util.concurrent.Executors

@SpringBootApplication(proxyBeanMethods = false)
class JettyHttpClientApplication {
    @Bean
    fun normalRestClient(
        restClientBuilder: RestClient.Builder,
    ) = RestClient.builder()
        .requestFactory(createRequestFactory())
        .baseUrl("http://localhost:8080")
        .build()

    private fun createRequestFactory(): JettyClientHttpRequestFactory {
        val httpClient = HttpClient()
            .apply {
                executor = Executors.newVirtualThreadPerTaskExecutor()
            }
        return JettyClientHttpRequestFactory(httpClient)
    }
}

fun main(args: Array<String>) {
    runApplication<JettyHttpClientApplication>(*args)
}

@Component
@Profile("init-clients")
class Warmup(restClients: ObjectProvider<RestClient>) {
    init {
        restClients.forEach { client ->
            runCatching {
                client.options()
                    .retrieve()
                    .toBodilessEntity()
            }.getOrNull()
        }
    }
}
