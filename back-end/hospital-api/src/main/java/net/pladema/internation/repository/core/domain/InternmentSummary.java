package net.pladema.internation.repository.core.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Getter
@Setter
@ToString
public class InternmentSummary {

    private Integer id;

    private Integer bedId;

    private String bedNumber;

    private Integer roomId;

    private String roomNumber;

    private Integer clinicalSpecialtyId;

    private String specialty;

    private LocalDateTime createdOn;

    private Integer healthcareProfessionalId;

    private String firstName;

    private String lastName;

    private int totalInternmentDays;

    public InternmentSummary(Integer id, LocalDateTime createdOn,
                             Integer bedId, String bedNumber, Integer roomId, String roomNumber,
                             Integer clinicalSpecialtyId, String specialty, Integer healthcareProfessionalId) {
        this.id = id;
        this.bedId = bedId;
        this.bedNumber = bedNumber;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.clinicalSpecialtyId = clinicalSpecialtyId;
        this.specialty = specialty;
        this.createdOn = createdOn;
        this.healthcareProfessionalId = healthcareProfessionalId;
        this.totalInternmentDays = totalInternmentDays();
    }

    private int totalInternmentDays(){
        LocalDate today = LocalDate.now();
        Period p = Period.between(getCreatedOn().toLocalDate(), today);
        return p.getDays();
    }
}
