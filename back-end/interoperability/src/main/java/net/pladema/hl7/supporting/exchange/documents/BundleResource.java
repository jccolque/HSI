//
//FHIR Documents.
//FHIR resources can be used to build documents that represent a composition: a coherent set
//of information that is a statement of healthcare information. A document is an immutable
//set of resources with a fixed presentation that is authored and/or attested by humans,
//organizations and devices.
//FHIR documents are for documents that are authored and assembled in FHIR, while the
// document reference resource is for general references to pre-existing documents.
//

package net.pladema.hl7.supporting.exchange.documents;

import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value="ws.renaper.enabled", havingValue = "true")
public class BundleResource {

    @Value("${app.default.language}")
    private String language;

    @Value("${ws.renaper.dominio}")
    private String dominio;


    @Autowired
    public BundleResource() {
        super();
    }
    
    public Bundle getExistingDocumentsReferences (TokenParam subject,
                                                  TokenParam custodian, TokenParam type) {
        return null;
    }

    public Bundle assembleDocument(IdType id) {
        return null;
    }
}
