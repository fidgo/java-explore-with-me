package ru.practicum.ewm.http.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
@Service
public class BaseClient {

    private final WebClient webClient;

    @Autowired
    public BaseClient(@Value("${statserv.url}") String serverUrl) {
        webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .build();
    }

    public EndPointHit hit(EndPointHit endPointHit) {
        return webClient
                .post()
                .uri("/hit")
                .body(BodyInserters.fromValue(endPointHit))
                .retrieve()
                .bodyToMono(EndPointHit.class)
                .block();
    }

    public List<ViewStats> get(String start, String end,
                                List<String> uris, Boolean unique) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", commaSeparatedUris(uris))
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToFlux(ViewStats.class)
                .collectList()
                .block();
    }

    private String commaSeparatedUris(List<String> uris) {
        return String.join(", ", uris)
                .replace("{", "")
                .replace("}", "");
    }
}
