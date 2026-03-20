package com.pokemon.api.oak.infrastructure.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PokemonKnowledgeService {

    private final VectorStore vectorStore;

    public void ingest(Resource pdfResource) {
        log.info("Ingesting PDF: {}", pdfResource.getFilename());

        var reader = new TikaDocumentReader(pdfResource);
        List<Document> rawDocs = reader.get();

        var splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(rawDocs);

        vectorStore.add(chunks);

        log.info("Ingested {} chunks from PDF", chunks.size());
    }

    public List<Document> search(String query) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(4)
                        .build()
        );
    }
}