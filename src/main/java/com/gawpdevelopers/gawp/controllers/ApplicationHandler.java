package com.gawpdevelopers.gawp.controllers;

import com.gawpdevelopers.gawp.domain.*;
import com.gawpdevelopers.gawp.services.*;
import com.gawpdevelopers.gawp.commands.ApplicationForm;
import com.gawpdevelopers.gawp.commands.DocumentForm;
import com.gawpdevelopers.gawp.converters.ApplicationToApplicationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Controller class that responds to the /application/* requests.
 */
@Controller
public class ApplicationHandler {
    private ApplicationService applicationService;

    private ApplicationToApplicationForm applicationToApplicationForm;
    private AdvertService advertService;
    private ApplicantService applicantService;
    private StorageService storageService;
    private DocumentService documentService;

    @Autowired
    public void setApplicationToApplicationForm(ApplicationToApplicationForm applicationToApplicationForm) {
        this.applicationToApplicationForm = applicationToApplicationForm;
    }
    @Autowired
    public void setApplicantService(ApplicantService applicantService) {
        this.applicantService = applicantService;
    }

    @Autowired
    public void setApplicationService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Autowired
    public void setAdvertService(AdvertService advertService) {
        this.advertService = advertService;
    }

    @Autowired
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    @Autowired
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    //    @RequestMapping("/application")
//    public String redirToList(){
//        return "redirect:/application/list";
//    }

//    @RequestMapping("/applicant")
//    public String applicantMenu(Model model){
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//
//        model.addAttribute("name",auth.getDetails() );
//        return "applicant/main-page-applicant";
//    }

    //  Applicant Mapping

    @RequestMapping("/applicant")
    public String applicantMainMenu(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Applicant applicant = applicantService.getByApiId(auth.getName());
        model.addAttribute("name", applicant.getUserName());

        return "applicant/main-page-applicant";
    }

    @RequestMapping({"/application/list", "/application"})
    public String listApplications(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Applicant applicant = applicantService.getByApiId(auth.getName());
        model.addAttribute("applications", applicationService.listAllByApplicant(applicant));
        return "application/list";
    }

    @RequestMapping("/application/show/{id}")
    public String getApplication(@PathVariable String id, Model model){
        Application application = applicationService.getById(Long.valueOf(id));
        model.addAttribute("applicationToShow", application);
        return "application/show";
    }

    @RequestMapping("application/edit/{id}")
    public String edit(@PathVariable String id, Model model){
        Application application = applicationService.getById(Long.valueOf(id));
        ApplicationForm applicationForm = applicationToApplicationForm.convert(application);

        model.addAttribute("ApplicationForm", applicationForm);
        return "application/applicationform";
    }

    @RequestMapping("/applicant/application/new/{advert_id}")
    public String newApplication(@PathVariable Long advert_id, Model model){

        ApplicationForm appForm = new ApplicationForm();
        appForm.setAdvert(advertService.getById(advert_id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Applicant applicant = applicantService.getByApiId(auth.getName());
        appForm.setApplicant(applicant);

        model.addAttribute("applicationForm", appForm);
        /**System.out.println("232 app new");
        System.out.println(appForm.getId());*/
//        System.out.println("Advert is null: " + advert == null);
//        model.addAttribute("target_advert", advert);
//        return "application/applicationform";
        return "applicant/new-application";
    }

    @RequestMapping("/applicant/application/new/success")
    public String applicationSuccess(){
        return "applicant/succesful-application";
    }

    @RequestMapping(value = "/application", method = RequestMethod.POST)
    public String saveOrUpdateApplication(@Valid ApplicationForm applicationForm, BindingResult bindingResult,
                                          @RequestParam("photo") MultipartFile photo,
                                          @RequestParam("transcript") MultipartFile transcript,
                                          @RequestParam("ales") MultipartFile ales,
                                          @RequestParam("referenceLetter") MultipartFile referenceLetter,
                                          @RequestParam(value = "permissionLetter", required = false) MultipartFile permissionLetter,
                                          @RequestParam(value = "passport", required = false) MultipartFile passport,
                                          @RequestParam(value = "masterTranscript", required = false) MultipartFile masterTranscript) {
        System.out.println("UPLOAD ZAMANI");
//        if(bindingResult.hasErrors()){
//            return "/applicant";
//        }

        applicationForm.setStatus(ApplicationStatus.WAITINGFORCONTROL);
        applicationForm.setLastUpdateDate(new Date());
        applicantService.saveOrUpdate(applicationForm.getApplicant());
        Application savedApplication = applicationService.saveOrUpdateApplicationForm(applicationForm);
        System.out.println(savedApplication.getStatus());

        Integer applicationId = Math.toIntExact(savedApplication.getId());

        //  For each file, create corresponding Document entry in the db.
        DocumentForm photoForm = new DocumentForm();
        photoForm.setDocType(DocumentType.PHOTO);
        photoForm.setApplication(savedApplication);
        photoForm.setPath(storageService.store(photo, applicationId).toString());
        documentService.saveOrUpdateDocumentForm(photoForm);

        DocumentForm transcriptForm = new DocumentForm();
        transcriptForm.setDocType(DocumentType.TRANSCRIPT);
        transcriptForm.setApplication(savedApplication);
        transcriptForm.setPath(storageService.store(transcript, applicationId).toString());
        documentService.saveOrUpdateDocumentForm(transcriptForm);

        DocumentForm alesForm = new DocumentForm();
        alesForm.setDocType(DocumentType.ALES);
        alesForm.setApplication(savedApplication);
        alesForm.setPath(storageService.store(ales, applicationId).toString());
        documentService.saveOrUpdateDocumentForm(alesForm);

        DocumentForm referenceLetterForm = new DocumentForm();
        referenceLetterForm.setDocType(DocumentType.REFERENCELETTER);
        referenceLetterForm.setApplication(savedApplication);
        referenceLetterForm.setPath(storageService.store(referenceLetter, applicationId).toString());
        documentService.saveOrUpdateDocumentForm(referenceLetterForm);

        if (permissionLetter != null && !permissionLetter.isEmpty()) {
            DocumentForm permissionLetterForm = new DocumentForm();
            permissionLetterForm.setDocType(DocumentType.PERMISSIONLETTER);
            permissionLetterForm.setApplication(savedApplication);
            permissionLetterForm.setPath(storageService.store(permissionLetter, applicationId).toString());
            documentService.saveOrUpdateDocumentForm(permissionLetterForm);
        }
        if (passport != null && !passport.isEmpty()) {
            DocumentForm passportForm = new DocumentForm();
            passportForm.setDocType(DocumentType.PASSPORT);
            passportForm.setApplication(savedApplication);
            passportForm.setPath(storageService.store(passport, applicationId).toString());
            documentService.saveOrUpdateDocumentForm(passportForm);
        }

        if (masterTranscript != null && !masterTranscript.isEmpty()) {
            DocumentForm masterTranscriptForm = new DocumentForm();
            masterTranscriptForm.setDocType(DocumentType.MASTERTRANSCRIPT);
            masterTranscriptForm.setApplication(savedApplication);
            masterTranscriptForm.setPath(storageService.store(masterTranscript, applicationId).toString());
            documentService.saveOrUpdateDocumentForm(masterTranscriptForm);
        }

        /**System.out.println("232 app update");
        System.out.println(applicationForm.getId());*/

//        return "redirect:/application/show/" + savedApplication.getId();
        return "redirect:/applicant/application/new/success";
    }

    @RequestMapping("/application/delete/{id}")
    public String delete(@PathVariable String id){
        applicationService.delete(Long.valueOf(id));
        return "redirect:/application/list";
    }

    //  Grad Mapping

    @RequestMapping("/grad/applicationsBeforeForwarding")
    public String applicationsBeforeForwarding(){
        return "/grad/application-before-forwarding-to-deparment";
    }

    @RequestMapping("/grad/applicationsBeforeForwarding/preReview")
    public String listPreReviewApplications(Model model){
        List<Application> applicationList= applicationService.listByStatus(ApplicationStatus.WAITINGFORCONTROL);
        model.addAttribute("applications",applicationList);

        return "/grad/applications-to-pre-review";

    }

    @RequestMapping("/grad/applicationsBeforeForwarding/declined")
    public String listDeclinedApplications(Model model){
        //TODO List declined applications and add it as attribute to model (DONE)
        List<Application> declinedApplications = applicationService.listByStatus(ApplicationStatus.REJECTED);
        model.addAttribute("declinedApplications", declinedApplications);

        return "/grad/declined-applications";
    }

    @RequestMapping("/grad/applicationsBeforeForwarding/verifiedAndApproved")
    public String listVerifiedAndApprovedApplications(Model model){
        //TODO List verified and approved applications and add it as attribute to model
        return "/grad/verified-and-approved-applications";
    }

    @RequestMapping("/grad/applicationsBeforeForwarding/verify")
    public String listApplicationsToVerify(Model model){
        //TODO List approved applications and add it as attribute to model
        return "/grad/verify-approved-applications";
    }

    //  Department Mapping

    @RequestMapping("/department/applicationsToInterview")
    public String listApplicationsToInterview(Model model){
        //  TODO Applications that belongs to this department should be shown.
        //      Or are they?

        List<Application> applications = applicationService.listAll();
        List<Application> applicationsToInterview =
                applications.stream()
                        .filter(application -> application.getInterview() == null && application.getStatus() == ApplicationStatus.VERIFIED)
                        .collect(Collectors.toList());

        model.addAttribute("applicationsToInterview", applicationsToInterview);

        return "department/applications-to-interview";
    }

    @RequestMapping("/department/interviewedApplications")
    public String listInterviewedApplications(Model model){
        //  TODO Applications that belongs to this department should be shown.
        //      Or are they?
        List<Application> applications = applicationService.listAll();
        List<Application> interviewedApplications =
                applications.stream()
                        .filter(application -> application.getInterview() != null && application.getStatus() == ApplicationStatus.VERIFIED)
                        .collect(Collectors.toList());

        model.addAttribute("interviewedApplications", interviewedApplications);

        return "department/interviewed-applications";

    }


}
