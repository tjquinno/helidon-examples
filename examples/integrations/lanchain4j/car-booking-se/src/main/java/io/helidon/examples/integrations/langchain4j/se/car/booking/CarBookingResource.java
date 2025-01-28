/*
 * Copyright (c) 2025 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.examples.integrations.langchain4j.se.car.booking;

import io.helidon.common.media.type.MediaTypes;
import io.helidon.examples.integrations.langchain4j.se.car.booking.ai.ChatAiService;
import io.helidon.examples.integrations.langchain4j.se.car.booking.ai.FraudAiService;
import io.helidon.integrations.langchain4j.Ai;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

import dev.langchain4j.model.chat.ChatLanguageModel;

/*
This is implemented as a singleton to support injection
 */
@Service.Singleton
public class CarBookingResource implements HttpService {

    private final ChatAiService aiService;

    private final FraudAiService fraudService;

    @Service.Inject
    public CarBookingResource(ChatAiService aiService,
                              FraudAiService fraudService,
                              @Service.Named("openai") ChatLanguageModel openAiChatModel,
                              ChatLanguageModel defaultChatModel) {
        // demonstrates we can inject the OpenAI specific chat language model, and a general model,
        // with the highest weight
        openAiChatModel.supportedCapabilities();
        defaultChatModel.supportedCapabilities();
        this.aiService = aiService;
        this.fraudService = fraudService;
    }

    @Override
    public void routing(HttpRules httpRules) {
        httpRules.get("/chat", this::chatWithAssistant)
                .get("/fraud", this::detectFraudForCustomer);
    }

    private void chatWithAssistant(ServerRequest req, ServerResponse res) {
        String question = req.query().get("question");
        res.headers().contentType(MediaTypes.TEXT_PLAIN);
        String answer;
        try {
            answer = aiService.chat(question);
        } catch (Exception e) {
            e.printStackTrace();
            answer = "My failure reason is:\n\n" + e.getMessage();
        }

        res.send(answer);
    }

    private void detectFraudForCustomer(ServerRequest req, ServerResponse res) {
        String name = req.query().get("name");
        String surname = req.query().get("surname");

        res.headers().contentType(MediaTypes.APPLICATION_JSON);
        res.send(fraudService.detectFraudForCustomer(name, surname));
    }

}
