package net.pladema.user.infrastructure.input.rest.mapper;

import net.pladema.user.infrastructure.input.rest.dto.UserDataDto;
import net.pladema.user.domain.UserDataBo;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public interface UserDataDtoMapper {

    @Named("UserDataBoToUserDataDto")
    UserDataDto UserDataBoToUserDataDto(UserDataBo user);
}
