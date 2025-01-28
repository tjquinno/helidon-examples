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

package io.helidon.examples.integrations.lanchain4j.mp.car.booking.ai;

import java.util.function.Supplier;

import io.helidon.service.registry.Service;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.inject.Named;

@Service.Singleton
@Service.Named("fraudChatMemory")
public class FraudChatMemoryFactory implements Supplier<ChatMemory> {
    @Override
    public ChatMemory get() {
        return MessageWindowChatMemory.withMaxMessages(5);
    }
}
