package com.vn.smartdata.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.vn.smartdata.model.User;


/**
 * The Class PasswordTokenManager.
 *
 * @author ivangsa
 */
@Component("newPasswordTokenManager")
public class PasswordTokenManager {
    
    /** The log. */
    private final Log log = LogFactory.getLog(PasswordTokenManager.class);

    /** The expiration time format. */
    private final SimpleDateFormat expirationTimeFormat = new SimpleDateFormat("yyyyMMddHHmm");
    
    /** The expiration time token length. */
    private final int expirationTimeTokenLength = expirationTimeFormat.toPattern().length();

    /** The password token encoder. */
    @Qualifier("passwordTokenEncoder")
    @Autowired
    private PasswordEncoder passwordTokenEncoder;

    /**
     * {@inheritDoc}
     */
    public String generateRecoveryToken(final User user) {
        if (user != null) {
            final String tokenSource = getTokenSource(user);
            final String expirationTimeStamp = expirationTimeFormat.format(getExpirationTime());
            return expirationTimeStamp + passwordTokenEncoder.encode(expirationTimeStamp + tokenSource);
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isRecoveryTokenValid(final User user, final String token) {
        if (user != null && token != null) {
            final String expirationTimeStamp = getTimestamp(token);
            final String tokenWithoutTimestamp = getTokenWithoutTimestamp(token);
            final String tokenSource = expirationTimeStamp + getTokenSource(user);
            final Date expirationTime = parseTimestamp(expirationTimeStamp);

            return expirationTime != null && expirationTime.after(new Date())
                    && passwordTokenEncoder.matches(tokenSource, tokenWithoutTimestamp);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void invalidateRecoveryToken(User user, String token) {
        // NOP
    }

    /**
     * Return tokens expiration time, now + 1 day.
     *
     * @return the expiration time
     */
    private Date getExpirationTime() {
        return DateUtils.addDays(new Date(), 1);
    }

    /**
     * Gets the timestamp.
     *
     * @param token the token
     * @return the timestamp
     */
    private String getTimestamp(final String token) {
        return StringUtils.substring(token, 0, expirationTimeTokenLength);
    }

    /**
     * Gets the token source.
     *
     * @param user the user
     * @return the token source
     */
    private String getTokenSource(final User user) {
        return user.getEmail() + user.getVersion() + user.getPassword();
    }

    /**
     * Gets the token without timestamp.
     *
     * @param token the token
     * @return the token without timestamp
     */
    private String getTokenWithoutTimestamp(final String token) {
        return StringUtils.substring(token, expirationTimeTokenLength, token.length());
    }

    /**
     * Parses the timestamp.
     *
     * @param timestamp the timestamp
     * @return the date
     */
    private Date parseTimestamp(final String timestamp) {
        try {
            return expirationTimeFormat.parse(timestamp);
        } catch (final ParseException e) {
            return null;
        }
    }

}
