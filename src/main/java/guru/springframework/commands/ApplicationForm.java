package guru.springframework.commands;

import guru.springframework.domain.Advert;
import guru.springframework.domain.ApplicationStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class ApplicationForm {
    private Long id;
    private Advert advert;  // Advert ID - Will be Set Later
    private Long intID;     //  Interview ID - Will be Set Later
    private Long applicantID;
    private ApplicationStatus status;
    private String applicationDegree; //TODO Keep it or lose it.
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastUpdateDate;
    private List<Integer> docIDs;   //TODO Plan is to keep documents in different folders for all applications.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public Long getIntID() {
        return intID;
    }

    public void setIntID(Long intID) {
        this.intID = intID;
    }

    public Long getApplicantID() {
        return applicantID;
    }

    public void setApplicantID(Long applicantID) {
        this.applicantID = applicantID;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getApplicationDegree() {
        return applicationDegree;
    }

    public void setApplicationDegree(String applicationDegree) {
        this.applicationDegree = applicationDegree;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<Integer> getDocIDs() {
        return docIDs;
    }

    public void setDocIDs(List<Integer> docIDs) {
        this.docIDs = docIDs;
    }
}