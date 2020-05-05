package net.pladema.internation.service.documents.anamnesis.impl;

import net.pladema.internation.service.internment.InternmentEpisodeService;
import net.pladema.internation.service.general.NoteService;
import net.pladema.internation.service.documents.DocumentService;
import net.pladema.internation.service.ips.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UpdateAnamnesisServiceImplTest {

	private static final String TOKEN = "TOKEN";

	private UpdateAnamnesisServiceImpl updateAnamnesisServiceImpl;

	@MockBean
	private DocumentService documentService;

	@MockBean
	private InternmentEpisodeService internmentEpisodeService;

	@MockBean
	private NoteService noteService;

	@MockBean
	private HealthConditionService healthConditionService;

	@MockBean
	private AllergyService allergyService;

	@MockBean
	private MedicationService medicationService;

	@MockBean
	private ClinicalObservationService clinicalObservationService;

	@MockBean
	private InmunizationService inmunizationService;

	@Before
	public void setUp() {
		updateAnamnesisServiceImpl = new UpdateAnamnesisServiceImpl(documentService, internmentEpisodeService,
				noteService, healthConditionService, allergyService, medicationService, clinicalObservationService,
				inmunizationService);
	}


	@Test
	public void test() {
	}
}
