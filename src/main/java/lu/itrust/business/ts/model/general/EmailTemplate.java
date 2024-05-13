package lu.itrust.business.ts.model.general;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.model.general.helper.EmailTemplateForm;

/**
 * Represents an email template.
 * This class is used to store information about an email template, including its email address, title, template content, and other properties.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EmailTemplate {

    @Id
    @Column(name = "idEmailTemplate")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "dtEmail", nullable = false)
    private String email;

    @Column(name = "dtTitle", nullable = false)
    private String title;

    @Column(name = "dtTemplate", nullable = false)
    private String template;

    @Column(name = "dtHtml", nullable = false)
    private boolean html = true;

    @Column(name = "dtInternalTime", nullable = false)
    private long internalTime = 1000;

    public EmailTemplate() {
    }

    public EmailTemplate(String email, String title, String template, boolean html) {
        this.email = email;
        this.html = html;
        this.title = title;
        this.template = template;
    }

    public EmailTemplate(EmailTemplate emailTemplate) {
        update(emailTemplate);
    }

    public EmailTemplate(EmailTemplateForm form) {
        update(form);
    }

    /**
     * Returns the ID of the email template.
     *
     * @return the ID of the email template
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the email template.
     *
     * @param id the ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the email associated with this EmailTemplate.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address for this EmailTemplate.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Checks if the email template is in HTML format.
     *
     * @return true if the email template is in HTML format, false otherwise.
     */
    public boolean isHtml() {
        return html;
    }

    /**
     * Sets whether the email template is in HTML format.
     *
     * @param html true if the email template is in HTML format, false otherwise
     */
    public void setHtml(boolean html) {
        this.html = html;
    }

    /**
     * Returns the title of the email template.
     *
     * @return the title of the email template
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the email template.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the template as a string.
     *
     * @return the template as a string
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Sets the email template.
     *
     * @param template the email template to set
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * Returns the internal time value.
     *
     * @return the internal time value
     */
    public long getInternalTime() {
        return internalTime;
    }

    /**
     * Sets the internal time for the email template.
     *
     * @param internalTime the internal time to set
     */
    public void setInternalTime(long internalTime) {
        this.internalTime = internalTime;
    }

    /**
     * Updates the email template with the values from the provided EmailTemplate object.
     *
     * @param emailTemplate The EmailTemplate object containing the updated values.
     */
    public void update(EmailTemplate emailTemplate) {
        this.email = emailTemplate.email;
        this.html = emailTemplate.html;
        this.title = emailTemplate.title;
        this.internalTime = emailTemplate.internalTime;
        this.template = emailTemplate.template;
    }

    /**
     * Updates the email template with the values from the provided form.
     *
     * @param form the form containing the updated values for the email template
     */
    public void update(EmailTemplateForm form) {
        this.email = form.getEmail();
        this.html = form.isHtml();
        this.title = form.getTitle();
        this.internalTime = form.getInternalTime();
        this.template = form.getTemplate();
    }

}
