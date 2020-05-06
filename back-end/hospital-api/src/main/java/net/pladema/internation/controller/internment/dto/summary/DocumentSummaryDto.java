package net.pladema.internation.controller.internment.dto.summary;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSummaryDto implements Serializable {

    private Long id;

    private boolean confirmed = false;
}
