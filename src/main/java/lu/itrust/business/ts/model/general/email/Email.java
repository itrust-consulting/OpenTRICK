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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }

}
