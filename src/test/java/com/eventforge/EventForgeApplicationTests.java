package com.eventforge;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventForgeApplicationTests {
    @LocalServerPort private int port;
    @Autowired private TestRestTemplate rest;

    @Test
    void healthIsPublic() {
        ResponseEntity<String> response = rest.getForEntity("http://localhost:" + port + "/health", String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void protectedEventsRequireAuthentication() {
        ResponseEntity<String> response = rest.getForEntity("http://localhost:" + port + "/api/events", String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }
}
