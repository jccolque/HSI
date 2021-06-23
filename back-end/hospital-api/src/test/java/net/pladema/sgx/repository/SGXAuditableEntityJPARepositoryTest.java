package net.pladema.sgx.repository;

import ar.lamansys.sgx.shared.auth.user.SgxUserDetails;
import net.pladema.UnitRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Optional;

import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class SGXAuditableEntityJPARepositoryTest extends UnitRepository {

    @Autowired
    private AuditableRepository repository;

    @MockBean
    private SecurityEvaluationContextExtension securityEvaluationContextExtension;

    @Before
    public void setUp() {
        when(securityEvaluationContextExtension.getRootObject()).thenReturn(new SecurityExpressionRoot(new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return 144;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean b) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        }) {
        });
    }

    @Test
    @DisplayName("Test soft delete")
    public void test_soft_delete() {
        Integer id = save(
                new AuditableClass(1))
                .getId();

        repository.deleteById(id);
        Optional<AuditableClass> result = repository.findById(id);
        Assertions.assertThat(result)
                  .isNotEmpty();

        Assertions.assertThat(result.get().isDeleted())
                .isTrue();

        Assertions.assertThat(result.get().getDeletedOn())
                .isNotNull();

        Assertions.assertThat(result.get().getDeletedBy())
                .isNotNull()
                .isEqualTo(144);
    }
}