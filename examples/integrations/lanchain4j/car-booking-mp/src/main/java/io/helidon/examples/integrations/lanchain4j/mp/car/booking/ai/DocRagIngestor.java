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

import java.nio.file.Path;
import java.util.List;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/*
This type is MP specific, as it does not need any of the SE features.
 */
@ApplicationScoped
public class DocRagIngestor {
    private static final String EMBEDDING_MODEL_NAME = "all-mini-lm-l6-v2";
    private static final System.Logger LOGGER = System.getLogger(DocRagIngestor.class.getName());
    @Produces
    @Named(EMBEDDING_MODEL_NAME) final EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    private final Path docsPath;
    private final EmbeddingStore<TextSegment> embeddingStore;

    @Inject
    public DocRagIngestor(@ConfigProperty(name = "app.docs-for-rag.dir") Path docs,
                          EmbeddingStore<TextSegment> embeddingStore) {
        this.docsPath = docs;
        this.embeddingStore = embeddingStore;
    }

    public void ingest(@Observes @Initialized(ApplicationScoped.class) Object pointless) {
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
}
