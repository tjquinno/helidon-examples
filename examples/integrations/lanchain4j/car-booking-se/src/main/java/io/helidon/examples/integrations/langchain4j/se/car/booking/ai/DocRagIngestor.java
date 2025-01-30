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

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

import io.helidon.common.config.Config;
import io.helidon.service.registry.Service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;

@Service.Singleton
public class DocRagIngestor {
    private static final String CONFIG_KEY = "app.docs-for-rag.dir";
    private static final System.Logger LOGGER = System.getLogger(DocRagIngestor.class.getName());

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    private final Path docsPath;

    @Service.Inject
    DocRagIngestor(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore, Config config) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;

        this.docsPath = config.get(CONFIG_KEY)
                .as(Path.class)
                .orElseThrow(() -> new IllegalStateException(CONFIG_KEY + " is a required configuration key for RAG"));
    }

    public void ingest() {
        LOGGER.log(System.Logger.Level.INFO, "DEMO ingesting documents from: {0}",
                   docsPath.toAbsolutePath().normalize());

        long start = System.currentTimeMillis();

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 30))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        List<Document> docs = loadDocs();
        ingestor.ingest(docs);

        LOGGER.log(System.Logger.Level.INFO, "DEMO {0} documents ingested in {1} msec",
                   docs.size(),
                   System.currentTimeMillis() - start);
    }

    private List<Document> loadDocs() {
        return FileSystemDocumentLoader.loadDocuments(docsPath, new TextDocumentParser());
    }

    /**
     * This is the embedding model we want to use.
     */
    @Service.Named("all-mini-lm-l6-v2")
    @Service.Singleton
    static class EmbeddingModelFactory implements Supplier<EmbeddingModel> {
        @Override
        public EmbeddingModel get() {
            return new AllMiniLmL6V2EmbeddingModel();
        }
    }
}
