package net.pladema.person.controller.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class BasicDataPersonDto implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4171735891439867357L;

	private Integer id;

    private String firstName;

    private String middleNames;

    private String lastName;

    private String otherLastNames;

    private Short identificationTypeId;

    private String identificationType;
    
    private String identificationNumber;
    
    private GenderDto gender;

    private Short age;

    private LocalDate birthDate;

    public String completeName(){
        return lastName + "," + firstName;
    }
}
