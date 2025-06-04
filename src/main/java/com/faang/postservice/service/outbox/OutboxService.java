package com.faang.postservice.service.outbox;

import java.util.UUID;

public interface OutboxService {

    void eventProcessing();

    void delete(UUID id);
}
