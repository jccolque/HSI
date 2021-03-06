package ar.lamansys.immunization;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(ImmunizationAutoConfiguration.class)
@Configuration
public @interface EnableImmunization {
}
