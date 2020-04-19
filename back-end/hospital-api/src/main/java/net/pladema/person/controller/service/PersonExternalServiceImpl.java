package net.pladema.person.controller.service;

import net.pladema.patient.controller.dto.APatientDto;
import net.pladema.patient.controller.dto.BasicPatientDto;
import net.pladema.person.controller.dto.BMPersonDto;
import net.pladema.person.controller.dto.BasicDataPersonDto;
import net.pladema.person.controller.mapper.PersonMapper;
import net.pladema.person.repository.entity.Person;
import net.pladema.person.repository.entity.PersonExtended;
import net.pladema.person.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonExternalServiceImpl implements PersonExternalService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final PersonService personService;

    private final PersonMapper personMapper;

    public PersonExternalServiceImpl(PersonService personService, PersonMapper personMapper){
        super();
        this.personService = personService;
        this.personMapper = personMapper;
        LOG.debug("{}", "created service");
    }

    @Override
    public BMPersonDto addPerson(APatientDto patient) {
        LOG.debug("Input parameters -> {}", patient);
        Person newPerson = personMapper.fromAPatientDto(patient);
        LOG.debug("Mapped result fromAPatientDto -> {}", newPerson);
        newPerson = personService.addPerson(newPerson);
        BMPersonDto result = personMapper.fromPerson(newPerson);
        LOG.debug("Mapped result fromPerson -> {}", result);
        LOG.debug("Output -> {}", result);
        return result;
    }


    @Override
    public void addPersonExtended(APatientDto patient, Integer personId, Integer addressId) {
        LOG.debug("Input parameters -> {}, {}, {}", patient, personId, addressId);
        PersonExtended personExtendedToAdd = personMapper.updatePersonExtendedPatient(patient, addressId);
        LOG.debug("Mapped result updatePersonExtendedPatient -> {}", personExtendedToAdd);
        personExtendedToAdd.setId(personId);
        personExtendedToAdd.setAddressId(addressId);
        LOG.debug("Going to add person extended -> {}", personExtendedToAdd);
        PersonExtended personExtendedSaved = personService.addPersonExtended(personExtendedToAdd);
        LOG.debug("PersonExtended added -> {}", personExtendedToAdd);
    }

    @Override
    public List<Integer> getPersonByDniAndGender(Short identificationTypeId, String identificationNumber, Short genderId) {
        List<Integer> result = personService.getPersonByDniAndGender(identificationTypeId, identificationNumber, genderId);
        return result;
    }

    @Override
    public BasicDataPersonDto getBasicDataPerson(Integer personId) {
        LOG.debug("Input parameters -> {}", personId);
        Person person = personService.getPerson(personId);
        BasicDataPersonDto result = personMapper.basicDatafromPerson(person);
        LOG.debug("Output -> {}", result);
        return result;
    }
}
