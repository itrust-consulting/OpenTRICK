package lu.itrust.business.ts.model.general.helper;

/**
 * Represents an email template form.
 */
public class EmailTemplateForm {
  
    private long id;

    private String email;

    private String title;

    private String template;

    private String format;

    private long internalTime;

    /**
     * Gets the ID of the email template.
     * 
     * @return the ID of the email template
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the email template.
     * 
     * @param id the ID of the email template
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the email address associated with the template.
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address associated with the template.
     * 
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the title of the email template.
     * 
     * @return the title of the email template
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the email template.
     * 
     * @param title the title of the email template
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the content template of the email.
     * 
     * @return the content template
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Sets the content template of the email.
     * 
     * @param template the content template
     */
    public void setTemplate(String template) {
        this.template = template;
    }

   
    /**
     * Gets the internal time of the email template.
     * 
     * @return the internal time of the email template
     */
    public long getInternalTime() {
        return internalTime;
    }

    /**
     * Sets the internal time of the email template.
     * 
     * @param internalTime the internal time of the email template
     */
    public void setInternalTime(long internalTime) {
        this.internalTime = internalTime;
    }

    /**
     * Gets the format of the email template.
     * 
     * @return the format of the email template
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the format of the email template.
     * 
     * @param format the format of the email template
     */
    public void setFormat(String format) {
        this.format = format;
    }

    
}
