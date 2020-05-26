package guru.springframework.services;

import guru.springframework.commands.DocumentForm;
import guru.springframework.converters.DocumentFormToDocument;
import guru.springframework.domain.Document;
import guru.springframework.domain.Document;
import guru.springframework.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;



@Service
public class DocumentServiceImpl implements DocumentService {

    private DocumentRepository documentRepository;
    private DocumentFormToDocument documentFormToDocument;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository, DocumentFormToDocument documentFormToDocument) {
        this.documentRepository = documentRepository;
        this.documentFormToDocument = documentFormToDocument;
    }



    @Override
    public List<Document> listAll() {
        List<Document> documents = new ArrayList<>();
        documentRepository.findAll().forEach(documents::add); //fun with Java 8
        return documents;
    }

    @Override
    public Document getById(Long id) {
        return documentRepository.findOne(id);
    }

    @Override
    public Document saveOrUpdate(Document document) {
        documentRepository.save(document);
        return document;
    }

    @Override
    public void delete(Long id) {
        documentRepository.delete(id);

    }

    @Override
    public Document saveOrUpdateDocumentForm(DocumentForm documentForm) {
        Document savedDocument = saveOrUpdate(documentFormToDocument.convert(documentForm));

        System.out.println("Saved Document Id: " + savedDocument.getId());
        return savedDocument;
    }

}