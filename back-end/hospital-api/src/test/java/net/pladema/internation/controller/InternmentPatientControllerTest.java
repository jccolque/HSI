package net.pladema.internation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import net.pladema.internation.repository.core.InternmentEpisodeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import net.pladema.BaseControllerTest;
import net.pladema.internation.controller.internment.InternmentPatientController;
import net.pladema.internation.controller.internment.mapper.InternmentEpisodeMapper;
import net.pladema.internation.service.internment.InternmentPatientService;

@RunWith(SpringRunner.class)
@WebMvcTest(InternmentPatientController.class)
public class InternmentPatientControllerTest extends BaseControllerTest {

	@MockBean
	private InternmentPatientService internmentPatientService;

	@MockBean
	private InternmentEpisodeMapper internmentEpisodeMapper;

	@MockBean
	private InternmentEpisodeRepository internmentEpisodeRepository;

	@Before
	public void setup() {
	}

	@Test
	@WithMockUser(authorities = {"ROOT"})
	public void getAllInternmentPatient() throws Exception {
		final Integer institutionId = 10;
		final String URL = "/institutions/ "+institutionId+"/internments/patients";
		mockMvc.perform(get(URL))
			.andDo(log())
			.andExpect(status().isOk());
			//.andExpect(jsonPath("$", hasSize(institutionId)));
	}
	
}
