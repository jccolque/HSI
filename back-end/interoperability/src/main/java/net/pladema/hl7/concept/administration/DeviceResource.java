package net.pladema.hl7.concept.administration;

import net.pladema.hl7.dataexchange.ISingleResourceFhir;
import net.pladema.hl7.dataexchange.model.adaptor.FhirID;
import net.pladema.hl7.supporting.conformance.InteroperabilityCondition;
import net.pladema.hl7.supporting.exchange.database.FhirPersistentStore;
import net.pladema.hl7.supporting.terminology.coding.CodingCode;
import net.pladema.hl7.supporting.terminology.coding.CodingSystem;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;

@Service
@Conditional(InteroperabilityCondition.class)
public class DeviceResource extends ISingleResourceFhir {

    @Autowired
    public DeviceResource(FhirPersistentStore store){
        super(store);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Device;
    }

    @Override
    public Device fetch(String id, Reference[] references) {

        Device resource = new Device();
        resource.setId(FhirID.autoGenerated());
        resource.addIdentifier(newIdentifier(resource, getIdentifier()));
        resource.setType(newCodeableConcept(CodingSystem.SNOMED, CodingCode.Device.TYPE));
        resource.setOwner(newReference(CodingSystem.REFES, id));
        resource.addDeviceName(new Device.DeviceDeviceNameComponent()
                .setName(getSystemName())
                .setType(Device.DeviceNameType.MANUFACTURERNAME));
        return resource;
    }

    private String getIdentifier() {
        String identifier = "HSI";
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("../pom-parent.xml"));
            String snapshot = (String) model.getProperties().get("revision");
            String version = snapshot.replace("-SNAPSHOT","");
            return identifier.concat("_").concat(version);
        }
        catch(IOException | XmlPullParserException ex){
            return identifier;
        }

    }
}
