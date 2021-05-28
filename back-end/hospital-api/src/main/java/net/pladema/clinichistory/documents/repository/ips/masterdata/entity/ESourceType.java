package net.pladema.clinichistory.documents.repository.ips.masterdata.entity;

import net.pladema.sgx.exceptions.NotFoundException;

public enum ESourceType {

    HOSPITALIZATION(0, "hospitalization"),
    OUTPATIENT(1, "outpatient"),
    RECIPE(2, "recipes"),
    ORDER(3, "orders"),
    EMERGENCY_CARE(4, "emergency_care"),
    ;

    private Short id;
    private String value;

    ESourceType(Number id, String value) {
        this.id = id.shortValue();
        this.value = value;;
    }
 
    public String getValue() {
        return value;
    }
    public Short getId() {
        return id;
    }

    public static ESourceType map(Short id) {
        for(ESourceType e : values()) {
            if(e.id.equals(id)) return e;
        }
        throw new NotFoundException("source-not-exists", String.format("La fuente de datos %s no existe", id));
    }

}
