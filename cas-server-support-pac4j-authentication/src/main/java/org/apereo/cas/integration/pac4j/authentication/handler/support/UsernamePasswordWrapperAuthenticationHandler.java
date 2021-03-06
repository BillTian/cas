package org.apereo.cas.integration.pac4j.authentication.handler.support;

import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.PrincipalNameTransformer;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.Authenticator;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.security.auth.login.AccountNotFoundException;
import java.security.GeneralSecurityException;

/**
 * Pac4j authentication handler which works on a CAS username / password credential
 * and uses a pac4j authenticator and profile creator to play authentication.
 *
 * @author Jerome Leleu
 * @since 4.2.0
 */
public class UsernamePasswordWrapperAuthenticationHandler
        extends AbstractWrapperAuthenticationHandler<UsernamePasswordCredential, UsernamePasswordCredentials> {

    /**
     * The underlying pac4j authenticator.
     */
    protected UsernamePasswordAuthenticator authenticator = new SimpleTestUsernamePasswordAuthenticator();

    /**
     * PasswordEncoder to be used by subclasses to encode passwords for
     * comparing against a resource.
     */
    private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();

    /**
     * PrincipalNameTransformer to be used by subclasses to transform the principal name.
     */
    private PrincipalNameTransformer principalNameTransformer = formUserId -> formUserId;

    /**
     * Default constructor.
     */
    public UsernamePasswordWrapperAuthenticationHandler() {

    }

    @Override
    protected UsernamePasswordCredentials convertToPac4jCredentials(final UsernamePasswordCredential casCredential)
            throws GeneralSecurityException, PreventedException {
        logger.debug("CAS credentials: {}", casCredential);

        final String username = this.principalNameTransformer.transform(casCredential.getUsername());
        if (username == null) {
            throw new AccountNotFoundException("Username is null.");
        }
        final String password = this.passwordEncoder.encode(casCredential.getPassword());
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password, getClass().getSimpleName());
        logger.debug("pac4j credentials: {}", credentials);
        return credentials;
    }

    @Override
    protected Authenticator getAuthenticator(final Credential credential) {
        return this.authenticator;
    }

    public void setAuthenticator(final UsernamePasswordAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    protected Class<UsernamePasswordCredential> getCasCredentialsType() {
        return UsernamePasswordCredential.class;
    }

    public PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }

    public void setPasswordEncoder(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public PrincipalNameTransformer getPrincipalNameTransformer() {
        return this.principalNameTransformer;
    }

    public void setPrincipalNameTransformer(final PrincipalNameTransformer principalNameTransformer) {
        this.principalNameTransformer = principalNameTransformer;
    }
}
