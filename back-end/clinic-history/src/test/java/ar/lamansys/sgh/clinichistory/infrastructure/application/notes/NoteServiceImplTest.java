package ar.lamansys.sgh.clinichistory.infrastructure.application.notes;

import ar.lamansys.sgh.clinichistory.UnitRepository;
import ar.lamansys.sgh.clinichistory.application.notes.NoteService;
import ar.lamansys.sgh.clinichistory.application.notes.NoteServiceImpl;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.NoteRepository;
import ar.lamansys.sgx.shared.strings.StringValidatorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NoteServiceImplTest extends UnitRepository {

    @Autowired
    private NoteRepository noteRepository;

    private NoteService noteService;


    @BeforeEach
    void setUp(){
        noteService = new NoteServiceImpl(noteRepository);
    }

    @Test
    void execute_maximumNoteLengthViolated() {
        String invalidString = "";
        for ( int i=0; i<1025; i++ ) invalidString += "a";
        String finalInvalidString = invalidString;
        Exception exception = Assertions.assertThrows(StringValidatorException.class, () ->
                noteService.createNote(finalInvalidString)
        );
        String expectedMessage = "El texto debe tener como máximo 1024 caracteres.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void execute_addNote_success (){
        String mock = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum";
        noteService.createNote(mock);
        Assertions.assertEquals(1, noteRepository.count());
    }


}
