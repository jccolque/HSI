package net.pladema.clinichistory.documents.service.hce;

import ar.lamansys.sgh.clinichistory.domain.hce.HCEImmunizationBo;

import java.util.List;

public interface HCEImmunizationService {

    List<HCEImmunizationBo> getImmunization(Integer patientId);
}
