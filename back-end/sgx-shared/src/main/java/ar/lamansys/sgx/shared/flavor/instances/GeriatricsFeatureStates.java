package ar.lamansys.sgx.shared.flavor.instances;

import ar.lamansys.sgx.shared.featureflags.AppFeature;
import ar.lamansys.sgx.shared.featureflags.states.InitialFeatureStates;

import java.util.EnumMap;

public class GeriatricsFeatureStates implements InitialFeatureStates {

	@Override
	public EnumMap<AppFeature, Boolean> getStates() {
		EnumMap<AppFeature, Boolean> map = new EnumMap<>(AppFeature.class);

		map.put(AppFeature.HABILITAR_ALTA_SIN_EPICRISIS, true);
		map.put(AppFeature.MAIN_DIAGNOSIS_REQUIRED, false);
		map.put(AppFeature.RESPONSIBLE_DOCTOR_REQUIRED, false);
		map.put(AppFeature.HABILITAR_CARGA_FECHA_PROBABLE_ALTA, false);
		map.put(AppFeature.HABILITAR_GESTION_DE_TURNOS, false);
		map.put(AppFeature.HABILITAR_HISTORIA_CLINICA_AMBULATORIA, false);
		map.put(AppFeature.HABILITAR_UPDATE_DOCUMENTS, false);
		map.put(AppFeature.HABILITAR_EDITAR_PACIENTE_COMPLETO, true);
		map.put(AppFeature.HABILITAR_MODULO_PORTAL_PACIENTE, false);
		map.put(AppFeature.HABILITAR_BUS_INTEROPERABILIDAD, false);
		map.put(AppFeature.HABILITAR_REPORTES, false);
		map.put(AppFeature.HABILITAR_VACUNAS_V2, false);

		return map;
	}
}
