package com.vn.smartdata.manager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vn.smartdata.exception.UserExistsException;
import com.vn.smartdata.mail.MailEngine;
import com.vn.smartdata.model.User;
import com.vn.smartdata.repository.UserRepository;
import com.vn.smartdata.service.UserService;


/**
 * Implementation of UserManager interface.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@Service("userManager")
public class UserManager extends GenericManager<User> implements UserService, UserDetailsService {
    
    /** The password encoder. */
    private PasswordEncoder passwordEncoder;

    /** The mail engine. */
    private MailEngine mailEngine;
    
    /** The message. */
    private SimpleMailMessage message;
    
    /** The password token manager. */
    private PasswordTokenManager passwordTokenManager;

    /** The password recovery template. */
    private String passwordRecoveryTemplate = "passwordRecovery.vm";
    
    /** The password updated template. */
    private String passwordUpdatedTemplate = "passwordUpdated.vm";
    
    /** The user repository. */
    private UserRepository userRepository;

	/**
	 * Sets the user repository.
	 *
	 * @param userRepository the new user repository
	 */
	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.repository = userRepository;
		this.userRepository = userRepository;
	}

    /**
     * Sets the password encoder.
     *
     * @param passwordEncoder the new password encoder
     */
    @Autowired
    @Qualifier("passwordEncoder")
    public void setPasswordEncoder(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Sets the mail engine.
     *
     * @param mailEngine the new mail engine
     */
    @Autowired(required = false)
    public void setMailEngine(final MailEngine mailEngine) {
        this.mailEngine = mailEngine;
    }

    /**
     * Sets the mail message.
     *
     * @param message the new mail message
     */
    @Autowired(required = false)
    public void setMailMessage(final SimpleMailMessage message) {
        this.message = message;
    }

    /**
     * Sets the password token manager.
     *
     * @param passwordTokenManager the new password token manager
     */
    @Autowired(required = false)
    public void setPasswordTokenManager(final PasswordTokenManager passwordTokenManager) {
        this.passwordTokenManager = passwordTokenManager;
    }

    /**
     * Velocity template name to send users a password recovery mail (default
     * passwordRecovery.vm).
     *
     * @param passwordRecoveryTemplate the Velocity template to use (relative to classpath)
     * @see org.appfuse.service.MailEngine#sendMessage(org.springframework.mail.SimpleMailMessage, String, java.util.Map)
     */
    public void setPasswordRecoveryTemplate(final String passwordRecoveryTemplate) {
        this.passwordRecoveryTemplate = passwordRecoveryTemplate;
    }

    /**
     * Velocity template name to inform users their password was updated
     * (default passwordUpdated.vm).
     *
     * @param passwordUpdatedTemplate the Velocity template to use (relative to classpath)
     * @see org.appfuse.service.MailEngine#sendMessage(org.springframework.mail.SimpleMailMessage, String, java.util.Map)
     */
    public void setPasswordUpdatedTemplate(final String passwordUpdatedTemplate) {
        this.passwordUpdatedTemplate = passwordUpdatedTemplate;
    }

    /**
     * {@inheritDoc}
     */
    public User getUser(final Long userId) {
        return userRepository.getUserById(userId);
    }

    /**
     * {@inheritDoc}
     */
    public User saveUser(final User user) throws UserExistsException {

        if (user.getVersion() == null) {
            // if new user, lowercase userId
            user.setUsername(user.getUsername().toLowerCase());
        }

        // Get and prepare password management-related artifacts
        boolean passwordChanged = false;
        if (passwordEncoder != null) {
            // Check whether we have to encrypt (or re-encrypt) the password
            if (user.getVersion() == null) {
                // New user, always encrypt
                passwordChanged = true;
            } else {
                // Existing user, check password in DB
                final String currentPassword = this.getUserPassword(user.getId());
                if (currentPassword == null) {
                    passwordChanged = true;
                } else {
                    if (!currentPassword.equals(user.getPassword())) {
                        passwordChanged = true;
                    }
                }
            }

            // If password was changed (or new user), encrypt it
            if (passwordChanged) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        } else {
            log.warn("PasswordEncoder not set, skipping password encryption...");
        }

        try {
            return this.save(user);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new UserExistsException("User '" + user.getUsername() + "' already exists!");
        }
    }
    
    /**
     * Gets the user password.
     *
     * @param userId the user id
     * @return the user password
     */
    public String getUserPassword (Long userId) {
    	User u = userRepository.getUserById(userId);
    	return u.getPassword();
    }

    /**
     * {@inheritDoc}
     */
    public void removeUser(final User user) {
        log.debug("removing user: " + user);
        this.remove(user);
    }

    /**
     * {@inheritDoc}
     */
    public void removeUser(final long userId) {
        log.debug("removing user: " + userId);
        this.remove(new Long(userId));
    }

    /**
     * {@inheritDoc}
     *
     * @param username the login name of the human
     * @return User the populated user object
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException thrown when username not found
     */
    public User getUserByUsername(final String username) {
        return (User) userRepository.getUserByUserName(username);
    }

    /**
     * {@inheritDoc}
     */
    /*public List<User> search(final String searchTerm) {
        return super.search(searchTerm, User.class);
    }*/

    public String buildRecoveryPasswordUrl(final User user, final String urlTemplate) {
        final String token = generateRecoveryToken(user);
        final String username = user.getUsername();
        return StringUtils.replaceEach(urlTemplate,
                new String[]{"{username}", "{token}"},
                new String[]{username, token});
    }

    /**
     * Generate recovery token.
     *
     * @param user the user
     * @return the string
     */
    public String generateRecoveryToken(final User user) {
        return passwordTokenManager.generateRecoveryToken(user);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRecoveryTokenValid(final String username, final String token) {
        return isRecoveryTokenValid(getUserByUsername(username), token);
    }

    /**
     * Checks if is recovery token valid.
     *
     * @param user the user
     * @param token the token
     * @return true, if is recovery token valid
     */
    public boolean isRecoveryTokenValid(final User user, final String token) {
        return passwordTokenManager.isRecoveryTokenValid(user, token);
    }

    /**
     * {@inheritDoc}
     */
    public void sendPasswordRecoveryEmail(final String username, final String urlTemplate) {
        log.debug("Sending password recovery token to user: " + username);

        final User user = getUserByUsername(username);
        final String url = buildRecoveryPasswordUrl(user, urlTemplate);

        sendUserEmail(user, passwordRecoveryTemplate, url);
    }

    /**
     * Send user email.
     *
     * @param user the user
     * @param template the template
     * @param url the url
     */
    private void sendUserEmail(final User user, final String template, final String url) {
        message.setTo(user.getFullName() + "<" + user.getEmail() + ">");

        final Map<String, Serializable> model = new HashMap<String, Serializable>();
        model.put("user", user);
        model.put("applicationURL", url);

        mailEngine.sendMessage(message, template, model);
    }


    /**
     * {@inheritDoc}
     */
    public User updatePassword(final String username, final String currentPassword, final String recoveryToken, final String newPassword, final String applicationUrl) throws UserExistsException {
        User user = getUserByUsername(username);
        if (isRecoveryTokenValid(user, recoveryToken)) {
            log.debug("Updating password from recovery token for user:" + username);
            user.setPassword(newPassword);
            user = saveUser(user);
            passwordTokenManager.invalidateRecoveryToken(user, recoveryToken);

            sendUserEmail(user, passwordUpdatedTemplate, applicationUrl);

            return user;
        } else if (StringUtils.isNotBlank(currentPassword)) {
            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                log.debug("Updating password (providing current password) for user:" + username);
                user.setPassword(newPassword);
                user = saveUser(user);
                return user;
            }
        }
        // or throw exception
        return null;
    }

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		return this.userRepository.getUserByUserName(username);
	}

	/* (non-Javadoc)
	 * @see com.vn.smartdata.service.UserService#getUser(java.lang.String)
	 */
	@Override
	public User getUser(String userId) {
		return this.get(Long.parseLong(userId));
	}

	/* (non-Javadoc)
	 * @see com.vn.smartdata.service.UserService#getUsers()
	 */
	@Override
	public List<User> getUsers() {
		return this.getAll();
	}

	/* (non-Javadoc)
	 * @see com.vn.smartdata.service.UserService#removeUser(java.lang.String)
	 */
	@Override
	public void removeUser(String userId) {
		this.remove(Long.parseLong(userId));
	}
}
