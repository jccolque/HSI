package net.pladema.user.infrastructure.input.rest;

import io.swagger.annotations.Api;
import net.pladema.user.application.createTokenPassword.CreateTokenPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "User-password-reset", tags = {"User password reset"})
@PreAuthorize("hasPermission(#institutionId, 'ADMINISTRADOR_INSTITUCIONAL_BACKOFFICE')")
public class UserPasswordResetController {
    private final Logger logger;
    private final CreateTokenPassword createTokenPassword;

    public UserPasswordResetController(CreateTokenPassword createTokenPassword) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.createTokenPassword = createTokenPassword;
    }

    @PostMapping(value = "/users/institution/{institutionId}/user/{userId}/password-reset")
    @Transactional
    public String create(@PathVariable(name = "institutionId") Integer institutionId,
                         @PathVariable(name = "userId") Integer userId) {
        logger.debug("Input parameters -> {}", userId);
        String result = createTokenPassword.run(userId);
        logger.debug("Output -> result {}", result);
        return result;
    }
}