package net.pladema.patient.controller.mapper;

import net.pladema.address.controller.dto.AddressDto;
import net.pladema.address.repository.entity.Address;
import net.pladema.patient.controller.dto.APatientDto;
import net.pladema.patient.controller.dto.BMPatientDto;
import net.pladema.patient.controller.dto.PatientSearchDto;
import net.pladema.patient.service.domain.PatientSearch;
import net.pladema.person.controller.dto.BMPersonDto;
import net.pladema.person.controller.mapper.PersonMapper;
import net.pladema.person.repository.entity.Person;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = PersonMapper.class)
public interface PatientMapper {

    public PatientSearchDto fromPatientSearch(PatientSearch patientSeach);

    public List<PatientSearchDto>
    fromListPatientSearch(List<PatientSearch> patientSeach);

    public AddressDto updatePatientAddress(APatientDto patient);

    public BMPatientDto fromPerson(BMPersonDto person);
    
}
