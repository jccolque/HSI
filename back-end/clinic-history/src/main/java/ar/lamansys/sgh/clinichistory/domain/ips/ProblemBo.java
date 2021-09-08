package ar.lamansys.sgh.clinichistory.domain.ips;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProblemBo extends HealthConditionBo {

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "{value.mandatory}")
    private String severity;

    private boolean chronic = false;

    public ProblemBo(SnomedBo snomedBo) {
        super(snomedBo);
    }
}
