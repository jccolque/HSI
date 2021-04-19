package net.pladema.clinichistory.documents.repository.ips.masterdata.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "allergy_intolerance_type")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AllergyIntoleranceType implements Serializable {

    @Id
    @Column(name = "id")
    private Short id;

    @Column(name = "name", nullable = false, length = 15)
    private String name;

    @Column(name = "display", nullable = false, length = 15)
    private String display;
}