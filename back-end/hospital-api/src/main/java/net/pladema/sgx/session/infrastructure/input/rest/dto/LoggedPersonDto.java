package net.pladema.sgx.session.infrastructure.input.rest.dto;

import javax.annotation.Nullable;

public class LoggedPersonDto {

	public final String firstName;

	public final String lastName;

	@Nullable
	public final String avatar;

	public LoggedPersonDto(String firstName, String lastName, String avatar) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.avatar = avatar;
	}
}
