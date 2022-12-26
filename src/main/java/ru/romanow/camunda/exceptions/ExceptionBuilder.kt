package ru.romanow.camunda.exceptions

import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono
import java.util.function.Function

fun <T : RuntimeException> buildEx(response: ClientResponse, func: Function<String?, T>): Mono<T> =
    response.bodyToMono(String::class.java)
        .flatMap { b -> Mono.error { func.apply(b) } }