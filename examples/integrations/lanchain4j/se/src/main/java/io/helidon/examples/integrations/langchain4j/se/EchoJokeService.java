package io.helidon.examples.integrations.langchain4j.se;

import java.util.function.Supplier;

import io.helidon.service.registry.Service;

@Service.PerLookup
@Service.RunLevel(Service.RunLevel.STARTUP)
class EchoJokeService {
    private final Supplier<Friend> friend;

    @Service.Inject
    EchoJokeService(Supplier<Friend> friend) {
        this.friend = friend;
    }

    @Service.PostConstruct
    void run() {
        var response = friend.get().chat("Tell me a joke about doctors in Czech Language");
        System.out.println("Joke about doctors in Czech Language:\n" + response);
    }
}
