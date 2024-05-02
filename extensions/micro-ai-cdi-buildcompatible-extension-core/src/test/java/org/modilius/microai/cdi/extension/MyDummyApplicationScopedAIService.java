package org.modilius.microai.cdi.extension;

import jakarta.enterprise.context.ApplicationScoped;
import org.modilius.microai.cdi.extension.spi.RegisterAIService;

@RegisterAIService(
        scope = ApplicationScoped.class
)
public interface MyDummyApplicationScopedAIService {

}
