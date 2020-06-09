package net.pladema.internation.repository.documents.searchdocument;

import net.pladema.sgx.repository.QueryPart;

public class DocumentDoctorSearchQuery extends DocumentSearchQuery {

    public DocumentDoctorSearchQuery(String plainText){
        super(plainText);
    }

    @Override
    public QueryPart where() {
        String pattern = escapeSqlText.toUpperCase();
        return super.where().concatPart(new QueryPart(
                "AND (UPPER(creator.firstName) LIKE '%"+pattern+"%' OR UPPER(creator.lastName) LIKE '%"+pattern+"%') \n"));
    }

    @Override
    public String noResultMessage(){
        return "No se encontraron documentos escritos por el médico " + plainText;
    }
}
