package lu.itrust.business.ts.model.general.email;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.model.general.document.impl.Attachment;

/**
 * Represents an email message.
 * 
 * This class provides the necessary properties and methods to create and manipulate email messages.
 * It includes properties for the email subject, body, recipients, attachments, and HTML flag.
 * 
 * The email message can be cached using the provided cache annotations.
 * 
 * Example usage:
 * 
 * Email email = new Email("Hello", "This is the body of the email.", true);
 * email.addRecipient(new Recipient("john@example.com"));
 * email.addAttachment(new Attachment("document.pdf"));
 * 
 * // Send the email
 * EmailService.send(email);
 */
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Email {

    @Id
    @Column(name = "idEmail", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "dtSubject", nullable = false)
    private String subject;

    @Column(name = "dtBody", nullable = false)
    private String body;

    @Column(name = "dtHtml", nullable = false)
    private boolean html;

    @OneToMany
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "fiEmail", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Recipient> recipients = new ArrayList<>();

    @OneToMany
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "fiEmail", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Attachment> attachments = new ArrayList<>();

    public Email() {
    }

    public Email(String subject, String body, boolean html) {
        this.subject = subject;
        this.body = body;
        this.html = html;
    }

    public Email(String subject, String body, boolean html, List<Recipient> recipients,
            List<Attachment> attachments) {
        this.body = body;
        this.html = html;
        this.subject = subject;
        this.recipients = recipients;
        this.attachments = attachments;
    }

    /**
     * Returns the ID of the email.
     *
     * @return the ID of the email
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the email.
     *
     * @param id the ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the subject of the email.
     *
     * @return the subject of the email
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject of the email.
     *
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Returns the body of the email.
     *
     * @return the body of the email
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the body of the email.
     *
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Returns the list of recipients of the email.
     *
     * @return the list of recipients of the email
     */
    public List<Recipient> getRecipients() {
        return recipients;
    }

    /**
     * Sets the list of recipients of the email.
     *
     * @param recipients the list of recipients to set
     */
    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

    /**
     * Returns the list of attachments of the email.
     *
     * @return the list of attachments of the email
     */
    public List<Attachment> getAttachments() {
        return attachments;
    }

    /**
     * Sets the list of attachments of the email.
     *
     * @param attachments the list of attachments to set
     */
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    /**
     * Returns whether the email is in HTML format.
     *
     * @return true if the email is in HTML format, false otherwise
     */
    public boolean isHtml() {
        return html;
    }

    /**
     * Sets whether the email is in HTML format.
     *
     * @param html true if the email is in HTML format, false otherwise
     */
    public void setHtml(boolean html) {
        this.html = html;
    }

}
