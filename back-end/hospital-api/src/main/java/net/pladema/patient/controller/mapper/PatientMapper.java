package net.pladema.patient.controller.mapper;

import net.pladema.address.controller.dto.AddressDto;
import net.pladema.patient.controller.dto.APatientDto;
import net.pladema.patient.controller.dto.BMPatientDto;
import net.pladema.patient.controller.dto.PatientSearchDto;
import net.pladema.patient.repository.domain.BasicListedPatient;
import net.pladema.patient.repository.entity.Patient;
import net.pladema.patient.service.domain.PatientSearch;
import net.pladema.person.controller.dto.BMPersonDto;
import net.pladema.person.controller.mapper.PersonMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {PersonMapper.class}, imports = {java.time.LocalDateTime.class,java.time.LocalTime.class})
public interface PatientMapper {

	public PatientSearchDto fromPatientSearch(PatientSearch patientSeach);

	public List<PatientSearchDto> fromListPatientSearch(List<PatientSearch> patientSeach);

	public AddressDto updatePatientAddress(APatientDto patient);

	public BMPatientDto fromPerson(BMPersonDto person);
	
	public Patient fromPatientDto(APatientDto patientDto);
	
	public BMPatientDto fromPatient(Patient patient);

    @Mapping(target = "id", source = "patientId")
    @Mapping( target="birthDate",  expression = "java(LocalDateTime.of(patient.getBirthDate(), LocalTime.now()))")
    public BMPatientDto fromBasicListedPatient(BasicListedPatient patient);

    public List<BMPatientDto> fromBasicListedPatientList(List<BasicListedPatient> patientsList);
    
}
