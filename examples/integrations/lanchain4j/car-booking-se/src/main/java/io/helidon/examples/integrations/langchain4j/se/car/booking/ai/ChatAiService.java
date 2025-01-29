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

package io.helidon.examples.integrations.langchain4j.se.car.booking.ai;

import io.helidon.integrations.langchain4j.Ai;

import dev.langchain4j.service.SystemMessage;

@Ai.Service
@Ai.ChatMemory("bookingChatMemory")
public interface ChatAiService {

    @SystemMessage("""
            You are a customer support agent of a car rental company named 'Miles of Smiles'.
            Before providing information about booking or canceling a booking, you MUST always check:
            booking number, customer name and surname.
            When a customer wants to cancel a booking, you must check his name and the Miles of Smiles cancellation policy first.
            Any cancellation request must comply with cancellation policy both for the delay and the duration.
            Today is {{current_date}}.
            """)
    String chat(String question);
}
