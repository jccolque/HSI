package net.pladema.sgh.app.security.infraestructure.configuration;

import ar.lamansys.sgx.auth.jwt.infrastructure.input.rest.filter.AuthenticationTokenFilter;
import ar.lamansys.sgx.auth.oauth.infrastructure.input.OAuth2AuthenticationFilter;
import ar.lamansys.sgx.shared.actuator.infrastructure.configuration.ActuatorConfiguration;
import net.pladema.permissions.repository.enums.ERole;
import net.pladema.sgh.app.security.infraestructure.filters.AuthorizationFilter;
import net.pladema.sgh.app.security.infraestructure.filters.PublicApiAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static final String RECAPTCHA = "/recaptcha";

	private static final String PASSWORD_RESET = "/password-reset";
	
	private static final String BACKOFFICE = "/backoffice";

	private static final String PUBLIC = "/public";

	private static final String[] SWAGGER_RESOURCES = {
			"/v3/**",
			"/swagger-ui/**"
	};

	private final AuthenticationTokenFilter authenticationTokenFilter;

	private final ActuatorConfiguration actuatorConfiguration;

	private final AuthorizationFilter authorizationFilter;

	private final PublicApiAuthenticationFilter publicApiAuthenticationFilter;

	private final OAuth2AuthenticationFilter oAuth2AuthenticationFilter;

	public WebSecurityConfiguration(AuthenticationTokenFilter authenticationTokenFilter,
									ActuatorConfiguration actuatorConfiguration,
									AuthorizationFilter authorizationFilter,
									PublicApiAuthenticationFilter publicApiAuthenticationFilter,
									OAuth2AuthenticationFilter oAuth2AuthenticationFilter) {
		this.authenticationTokenFilter = authenticationTokenFilter;
		this.actuatorConfiguration = actuatorConfiguration;
		this.authorizationFilter = authorizationFilter;
		this.publicApiAuthenticationFilter = publicApiAuthenticationFilter;
		this.oAuth2AuthenticationFilter = oAuth2AuthenticationFilter;
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		
		// @formatter:off
		httpSecurity.csrf().disable()
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeRequests()
				.antMatchers(HttpMethod.GET,  "/users/{id}/enable").permitAll()
				.antMatchers(HttpMethod.POST, "/users/activationlink/resend").permitAll()
				.antMatchers("/passwords/reset" ).permitAll()
				.antMatchers("/actuator/health").permitAll()
				.antMatchers("/actuator/env/**").hasAnyAuthority(
						ERole.ROOT.getValue(),
						ERole.ADMINISTRADOR.getValue())
				.antMatchers("/actuator/**").access(actuatorConfiguration.getAccessInfo())
				.antMatchers( "/auth/**").permitAll()
				.antMatchers(SWAGGER_RESOURCES).permitAll()
				.antMatchers(BACKOFFICE + "/properties").hasAnyAuthority(
						ERole.ROOT.getValue(),
						ERole.ADMINISTRADOR.getValue())
				.antMatchers(BACKOFFICE + "/**").hasAnyAuthority(
					ERole.ROOT.getValue(),
					ERole.ADMINISTRADOR.getValue(),
					ERole.ADMINISTRADOR_INSTITUCIONAL_BACKOFFICE.getValue())
				.antMatchers(RECAPTCHA + "/**").permitAll()
				.antMatchers("/oauth/**").permitAll()
				.antMatchers(HttpMethod.GET,PUBLIC + "/**").permitAll()
				.antMatchers(HttpMethod.POST, PASSWORD_RESET).permitAll()
				.antMatchers(HttpMethod.GET, "/bed/reports/**").permitAll()
				.antMatchers(HttpMethod.GET, "/assets/**").permitAll()
				.antMatchers("/public-api/**").hasAnyAuthority(ERole.API_CONSUMER.getValue())
				.antMatchers("/fhir/**").permitAll()
				.antMatchers("/**").authenticated()
		.anyRequest().authenticated();

		// @formatter:on
		httpSecurity.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
		httpSecurity.addFilterAfter(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
		httpSecurity.addFilterAfter(oAuth2AuthenticationFilter, AuthenticationTokenFilter.class);
		httpSecurity.addFilterAfter(publicApiAuthenticationFilter, OAuth2AuthenticationFilter.class);
		httpSecurity.addFilterAfter(authorizationFilter, PublicApiAuthenticationFilter.class);
	}

}

