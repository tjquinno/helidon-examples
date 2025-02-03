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

package io.helidon.examples.integrations.lanchain4j.mp.car.booking;

import io.helidon.examples.integrations.lanchain4j.mp.car.booking.ai.ChatAiService;
import io.helidon.examples.integrations.lanchain4j.mp.car.booking.ai.FraudAiService;
import io.helidon.examples.integrations.lanchain4j.mp.car.booking.model.FraudResponse;
import io.helidon.service.registry.Service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@ApplicationScoped
@Path("/car-booking")
public class CarBookingResource {

    private final ChatAiService aiService;

    private final FraudAiService fraudService;

    @Inject
    public CarBookingResource(ChatAiService aiService,
                              FraudAiService fraudService,
                              @Named("openai") ChatLanguageModel openAiChatModel,
                              @Named(Service.Named.DEFAULT_NAME) ChatLanguageModel defaultChatModel) {
        openAiChatModel.supportedCapabilities();
        defaultChatModel.supportedCapabilities();
        this.aiService = aiService;
        this.fraudService = fraudService;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/chat")
    @Operation(summary = "Chat with an assistant.",
               description = "Ask any car booking related question.",
               operationId = "chatWithAssistant")
    @APIResponse(responseCode = "200",
                 description = "Answer provided by assistant",
                 content = @Content(mediaType = "text/plain"))
    @Counted
    public String chatWithAssistant(
            @Parameter(description = "The question to ask the assistant", required = true, example = "I want to book a car how can you help me?")
            @QueryParam("question") String question) {
        String answer;
        try {
            answer = aiService.chat(question);
        } catch (Exception e) {
            e.printStackTrace();
            answer = "My failure reason is:\n\n" + e.getMessage();
        }

        return answer;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/fraud")
    public FraudResponse detectFraudForCustomer(
            @Parameter(description = "Name of the customer to detect fraud for.", required = true, example = "Bond")
            @QueryParam("name") String name,
            @Parameter(description = "Surname of the customer to detect fraud for.", required = true, example = "James")
            @QueryParam("surname") String surname) {
        return fraudService.detectFraudForCustomer(name, surname);
    }

}
