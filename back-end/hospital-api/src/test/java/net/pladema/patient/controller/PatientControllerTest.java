package net.pladema.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.pladema.BaseControllerTest;
import net.pladema.address.controller.service.AddressExternalService;
import net.pladema.internation.controller.InternmentPatientController;
import net.pladema.patient.controller.mapper.PatientMapper;
import net.pladema.patient.service.PatientService;
import net.pladema.person.controller.mapper.PersonMapper;
import net.pladema.person.controller.service.PersonExternalService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PatientController.class)
public class PatientControllerTest extends BaseControllerTest {

	@MockBean
	private PatientService patientService;

	@MockBean
	private PersonExternalService personExternalService;

	@MockBean
	private AddressExternalService addressExternalService;

	@MockBean
	private PatientMapper patientMapper;

	@Before
	public void setup() {
	}

	@Test
	public void test_getBasicData() throws Exception {
		final Integer patientId = 1;
		final String URL = "/patient/"+patientId +"/basicdata";
		when(patientService.getPatient(any())).thenReturn(Optional.empty());
		mockMvc.perform(get(URL))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(patientId));
	}
	
}
