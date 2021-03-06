package com.gawpdevelopers.gawp.converters;

import com.gawpdevelopers.gawp.commands.DocumentForm;
import com.gawpdevelopers.gawp.domain.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * TODO Not Used Yet
 */
@Component
public class DocumentFormToDocument implements Converter<DocumentForm, Document> {

    @Override
    public Document convert(DocumentForm documentForm) {
        Document document = new Document();
        if (documentForm.getId() != null  && !StringUtils.isEmpty(documentForm.getId())) {
            document.setId(new Long(documentForm.getId()));
        }
        document.setDocType(documentForm.getDocType());
        document.setPath(documentForm.getPath());
        document.setApplication(documentForm.getApplication());
        return document;
    }
}
