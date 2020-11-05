//
//All documents have the same structure: a Bundle of resources of type "document" that
// has a Composition resource as the first resource in the bundle, followed by a series of
// other resources, referenced from the Composition resource, that provide supporting
// evidence for the document.
//

package net.pladema.hl7.supporting.exchange.documents;

import net.pladema.hl7.dataexchange.IResourceFhir;
import net.pladema.hl7.dataexchange.model.adaptor.FhirCode;
import net.pladema.hl7.dataexchange.model.adaptor.FhirID;
import net.pladema.hl7.dataexchange.model.adaptor.FhirNarrative;
import net.pladema.hl7.dataexchange.model.domain.CompositionVo;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompositionResource extends IResourceFhir {

    @Autowired
    public CompositionResource(){
        super();
    }

    public static Composition metadatos(CompositionVo data, Reference[] references){
        Composition resource = new Composition();
        resource.setId(FhirID.autoGenerated());

        //======================Header=======================
        resource.setSubject(references[0]);
        resource.addAuthor(references[1]);
        //firmante o responsable del documento.
        resource.addAttester(new Composition.CompositionAttesterComponent()
                .setMode(Composition.CompositionAttestationMode.LEGAL)
                .setTime(data.getCreatedOn())
                .setParty(references[2]));
        //Organización a cargo
        resource.setCustodian(references[2]);

        //resource.setIdentifier(newIdentifier(dominio, resource));
        resource.setStatus(Composition.CompositionStatus.FINAL);
        resource.setType(newCodeableConcept(data.getType()));

        resource.setDate(data.getCreatedOn());
        resource.setTitle(data.getTitle());

        resource.setConfidentiality(Composition.DocumentConfidentiality.fromCode(data.getConfidentiality()));
        return resource;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Composition;
    }

    /**
     *
     * @param system
     * @param code
     * @param title
     * @param entries entries for the same resource type
     * @return specific section
     */
    public static Composition.SectionComponent newSection(String system, FhirCode code,
                                                          String title,
                                                          List<Bundle.BundleEntryComponent> entries){
        Composition.SectionComponent section = new Composition.SectionComponent()
                .setCode(newCodeableConcept(system, code))
                .setTitle(title)
                .setText(FhirNarrative.buildNarrative(entries
                        .stream()
                        .map(Bundle.BundleEntryComponent::getResource)
                        .collect(Collectors.toList()))
                );
        entries.forEach((entry) ->
            section.addEntry().setReference(entry.getFullUrl())
        );
        return section;
    }
}
