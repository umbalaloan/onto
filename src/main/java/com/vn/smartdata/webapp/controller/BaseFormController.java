package com.vn.smartdata.webapp.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.appfuse.Constants;
import org.appfuse.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.vn.smartdata.mail.MailEngine;
import com.vn.smartdata.manager.UserManager;


/**
 * Implementation of <strong>SimpleFormController</strong> that contains
 * convenience methods for subclasses.  For example, getting the current
 * user and saving messages/errors. This class is intended to
 * be a base class for all Form controllers.
 *
 * <p><a href="BaseFormController.java.html"><i>View Source</i></a></p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class BaseFormController implements ServletContextAware {
    
    /** The Constant MESSAGES_KEY. */
    public static final String MESSAGES_KEY = "successMessages";
    
    /** The Constant ERRORS_MESSAGES_KEY. */
    public static final String ERRORS_MESSAGES_KEY = "errors";
    
    /** The log. */
    protected final transient Log log = LogFactory.getLog(getClass());
    
    /** The user manager. */
    @Autowired
    private UserManager userManager = null;
    
    /** The mail engine. */
    protected MailEngine mailEngine = null;
    
    /** The message. */
    protected SimpleMailMessage message = null;
    
    /** The template name. */
    protected String templateName = "accountCreated.vm";
    
    /** The cancel view. */
    protected String cancelView;
    
    /** The success view. */
    protected String successView;

    /** The messages. */
    private MessageSourceAccessor messages;
    
    /** The servlet context. */
    private ServletContext servletContext;

    /** The validator. */
    @Autowired(required = false)
    Validator validator;

    /**
     * Sets the messages.
     *
     * @param messageSource the new messages
     */
    @Autowired
    public void setMessages(MessageSource messageSource) {
        messages = new MessageSourceAccessor(messageSource);
    }

    /**
     * Sets the user manager.
     *
     * @param userManager the new user manager
     */
    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Gets the user manager.
     *
     * @return the user manager
     */
    public UserManager getUserManager() {
        return this.userManager;
    }

    /**
     * Save error.
     *
     * @param request the request
     * @param error the error
     */
    @SuppressWarnings("unchecked")
    public void saveError(HttpServletRequest request, String error) {
        List errors = (List) request.getSession().getAttribute(ERRORS_MESSAGES_KEY);
        if (errors == null) {
            errors = new ArrayList();
        }
        errors.add(error);
        request.getSession().setAttribute(ERRORS_MESSAGES_KEY, errors);
    }
    
    /**
     * Save message.
     *
     * @param request the request
     * @param msg the msg
     */
    @SuppressWarnings("unchecked")
    public void saveMessage(HttpServletRequest request, String msg) {
        List messages = (List) request.getSession().getAttribute(MESSAGES_KEY);

        if (messages == null) {
            messages = new ArrayList();
        }

        messages.add(msg);
        request.getSession().setAttribute(MESSAGES_KEY, messages);
    }

    /**
     * Convenience method for getting a i18n key's value.  Calling
     * getMessageSourceAccessor() is used because the RequestContext variable
     * is not set in unit tests b/c there's no DispatchServlet Request.
     *
     * @param msgKey the msg key
     * @param locale the current locale
     * @return the text
     */
    public String getText(String msgKey, Locale locale) {
        return messages.getMessage(msgKey, locale);
    }

    /**
     * Convenient method for getting a i18n key's value with a single
     * string argument.
     *
     * @param msgKey the msg key
     * @param arg the arg
     * @param locale the current locale
     * @return the text
     */
    public String getText(String msgKey, String arg, Locale locale) {
        return getText(msgKey, new Object[] { arg }, locale);
    }

    /**
     * Convenience method for getting a i18n key's value with arguments.
     *
     * @param msgKey the msg key
     * @param args the args
     * @param locale the current locale
     * @return the text
     */
    public String getText(String msgKey, Object[] args, Locale locale) {
        return messages.getMessage(msgKey, args, locale);
    }

    /**
     * Convenience method to get the Configuration HashMap
     * from the servlet context.
     *
     * @return the user's populated form from the session
     */
    public Map getConfiguration() {
        Map config = (HashMap) servletContext.getAttribute(Constants.CONFIG);

        // so unit tests don't puke when nothing's been set
        if (config == null) {
            return new HashMap();
        }

        return config;
    }

    // Spring MVC failed to convert property value in file upload form ----- Reference: http://www.mkyong.com/spring-mvc/spring-mvc-failed-to-convert-property-value-in-file-upload-form/
    /**
     * Set up a custom property editor for converting form inputs to real objects.
     *
     * @param request the current request
     * @param binder the data binder
     */
    @InitBinder
    protected void initBinder(HttpServletRequest request,
                              ServletRequestDataBinder binder) {
        binder.registerCustomEditor(Integer.class, null,
                                    new CustomNumberEditor(Integer.class, null, true));
        binder.registerCustomEditor(Long.class, null,
                                    new CustomNumberEditor(Long.class, null, true));
        binder.registerCustomEditor(byte[].class,
                                    new ByteArrayMultipartFileEditor());
        SimpleDateFormat dateFormat = 
            new SimpleDateFormat(getText("date.format", request.getLocale()));
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, null, 
                                    new CustomDateEditor(dateFormat, true));
    }

    /**
     * Convenience message to send messages to users, includes app URL as footer.
     * @param user the user to send a message to.
     * @param msg the message to send.
     * @param url the URL of the application.
     */
    protected void sendUserMessage(User user, String msg, String url) {
        if (log.isDebugEnabled()) {
            log.debug("sending e-mail to user [" + user.getEmail() + "]...");
        }

        message.setTo(user.getFullName() + "<" + user.getEmail() + ">");

        Map<String, Serializable> model = new HashMap<String, Serializable>();
        model.put("user", user);

        // TODO: once you figure out how to get the global resource bundle in
        // WebWork, then figure it out here too.  In the meantime, the Username
        // and Password labels are hard-coded into the template. 
        // model.put("bundle", getTexts());
        model.put("message", msg);
        model.put("applicationURL", url);
        mailEngine.sendMessage(message, templateName, model);
    }

    /**
     * Sets the mail engine.
     *
     * @param mailEngine the new mail engine
     */
    @Autowired
    public void setMailEngine(MailEngine mailEngine) {
        this.mailEngine = mailEngine;
    }

    /**
     * Sets the message.
     *
     * @param message the new message
     */
    @Autowired
    public void setMessage(SimpleMailMessage message) {
        this.message = message;
    }

    /**
     * Sets the template name.
     *
     * @param templateName the new template name
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
   
    /**
     * Sets the cancel view.
     *
     * @param cancelView the cancel view
     * @return the base form controller
     */
    public final BaseFormController setCancelView(String cancelView) {
        this.cancelView = cancelView;
        return this;
    }

    /**
     * Gets the cancel view.
     *
     * @return the cancel view
     */
    public final String getCancelView() {
        // Default to successView if cancelView is invalid
        if (this.cancelView == null || this.cancelView.length()==0) {
            return getSuccessView();
        }
        return this.cancelView;   
    }

    /**
     * Gets the success view.
     *
     * @return the success view
     */
    public final String getSuccessView() {
        return this.successView;
    }
    
    /**
     * Sets the success view.
     *
     * @param successView the success view
     * @return the base form controller
     */
    public final BaseFormController setSuccessView(String successView) {
        this.successView = successView;
        return this;
    }

    /* (non-Javadoc)
     * @see org.springframework.web.context.ServletContextAware#setServletContext(javax.servlet.ServletContext)
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * Gets the servlet context.
     *
     * @return the servlet context
     */
    protected ServletContext getServletContext() {
        return servletContext;
    }
}
