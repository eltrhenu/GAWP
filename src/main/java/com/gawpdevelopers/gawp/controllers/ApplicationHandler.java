package com.gawpdevelopers.gawp.controllers;

import com.gawpdevelopers.gawp.commands.ApplicationForm;
import com.gawpdevelopers.gawp.commands.DocumentForm;
import com.gawpdevelopers.gawp.converters.ApplicationToApplicationForm;
import com.gawpdevelopers.gawp.domain.*;
import com.gawpdevelopers.gawp.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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

import javax.print.Doc;
import javax.validation.Valid;
import java.util.List;


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
    private MailService emailService;

    @Autowired
    public void setEmailService(MailService emailService){
        this.emailService = emailService;
    }

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
        System.out.println("GELDİM ÇOK YAKINIM");
        ApplicationForm appForm = new ApplicationForm();
        appForm.setAdvert(advertService.getById(advert_id));
        model.addAttribute("applicationForm", appForm);
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
                                          @RequestParam("permissionLetter") MultipartFile permissionLetter,
                                          @RequestParam("passport") MultipartFile passport,
                                          @RequestParam("masterTranscript") MultipartFile masterTranscript) {
        System.out.println("UPLOAD ZAMANI");
        if(bindingResult.hasErrors()){
            return "/applicant";
        }

        applicationForm.setStatus(ApplicationStatus.WAITINGFORCONTROL);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Applicant applicant = applicantService.getByApiId(auth.getName());
        applicationForm.setApplicant(applicant);
        Application savedApplication = applicationService.saveOrUpdateApplicationForm(applicationForm);
        System.out.println(savedApplication.getStatus());

        //  For each file, create corresponding Document entry in the db.
        DocumentForm photoForm = new DocumentForm();
        photoForm.setDocType(DocumentType.PHOTO);
        photoForm.setApplication(savedApplication);
        photoForm.setPath(storageService.store(photo).toString());
        documentService.saveOrUpdateDocumentForm(photoForm);

        DocumentForm transcriptForm = new DocumentForm();
        photoForm.setDocType(DocumentType.TRANSCRIPT);
        photoForm.setApplication(savedApplication);
        photoForm.setPath(storageService.store(transcript).toString());
        documentService.saveOrUpdateDocumentForm(transcriptForm);

        DocumentForm alesForm = new DocumentForm();
        photoForm.setDocType(DocumentType.ALES);
        photoForm.setApplication(savedApplication);
        photoForm.setPath(storageService.store(ales).toString());
        documentService.saveOrUpdateDocumentForm(alesForm);

        DocumentForm referenceLetterForm = new DocumentForm();
        photoForm.setDocType(DocumentType.REFERENCELETTER);
        photoForm.setApplication(savedApplication);
        photoForm.setPath(storageService.store(referenceLetter).toString());
        documentService.saveOrUpdateDocumentForm(referenceLetterForm);
        //TODO what if applicant is not working, no need to upload this file
        DocumentForm permissionLetterForm = new DocumentForm();
        photoForm.setDocType(DocumentType.PERMISSIONLETTER);
        photoForm.setApplication(savedApplication);
        photoForm.setPath(storageService.store(permissionLetter).toString());
        documentService.saveOrUpdateDocumentForm(permissionLetterForm);
        //TODO what if applicant has turkish id card, no need to upload this file
        DocumentForm passportForm = new DocumentForm();
        photoForm.setDocType(DocumentType.PASSPORT);
        photoForm.setApplication(savedApplication);
        photoForm.setPath(storageService.store(passport).toString());
        documentService.saveOrUpdateDocumentForm(passportForm);

        DocumentForm masterTranscriptForm = new DocumentForm();
        photoForm.setDocType(DocumentType.MASTERTRANSCRIPT);
        photoForm.setApplication(savedApplication);
        photoForm.setPath(storageService.store(masterTranscript).toString());
        documentService.saveOrUpdateDocumentForm(masterTranscriptForm);

//        return "redirect:/application/show/" + savedApplication.getId();
        return "redirect:/applicant/application/new/success";
    }

    @RequestMapping("/application/delete/{id}")
    public String delete(@PathVariable String id){
        applicationService.delete(Long.valueOf(id));
        return "redirect:/application/list";
    }

    @RequestMapping("/grad/applicationsBeforeForwarding")
    public String applicationsBeforeForwarding(){
        return "/grad/application-before-forwarding-to-deparment";
    }

    @RequestMapping("/grad/applicationsBeforeForwarding/preReview")
    public String listPreReviewApplications(Model model){
        List<Application> applicationList= applicationService.listByStatus(ApplicationStatus.WAITINGFORCONTROL);
        applicationService.listByStatus(ApplicationStatus.MISSINGDOCUMENT).forEach(applicationList::add);
        model.addAttribute("applications",applicationList);

        return "/grad/applications-to-pre-review";

    }
    @RequestMapping("/grad/applicationsBeforeForwarding/preReview/{id}")
    public String reviewApplication(@PathVariable String id, Model model){
        Application application = applicationService.getById(Long.valueOf(id));
        model.addAttribute("applicationToReview", application);
        model.addAttribute("mail",new Mail());
        return "/grad/check-interview";
    }
    @RequestMapping("/setConfirm/{id}")
    public String setStatus(@PathVariable String id){
        Application application= applicationService.getById(Long.valueOf(id));
        application.setStatus(ApplicationStatus.CONFIRMED);
        Application saved = applicationService.saveOrUpdate(application);
        return "redirect:/grad/applicationsBeforeForwarding/preReview";

    }


    @RequestMapping(path="/ignore/{id}")
    public String decline(@PathVariable String id){

        System.out.println("Geliyorum");
        System.out.println("Rejected");
        Application application= applicationService.getById(Long.valueOf(id));
        application.setStatus(ApplicationStatus.REJECTED);
        Application saved = applicationService.saveOrUpdate(application);
        Applicant applicant =application.getApplicant();
        Mail mail = new Mail();
        System.out.println(mail.getContent());
        mail.setFrom("noreply@gawp.com");
        mail.setTo(applicant.getEmail());
        mail.setSubject("Size Spring ilen Salamlar getirmişem");
        mail.setContent("rejected");
        emailService.sendSimpleMessage(mail);
        return "redirect:/grad/applicationsBeforeForwarding/preReview";

    }



    @RequestMapping(path="/notify/{id}")
    public String notify(@PathVariable String id){
        //TODO notify için mail-message i fronttan alınması lazım
        Application application= applicationService.getById(Long.valueOf(id));
        application.setStatus(ApplicationStatus.MISSINGDOCUMENT);
        Application saved = applicationService.saveOrUpdate(application);
        Applicant applicant =application.getApplicant();
        Mail mail = new Mail();
        mail.setFrom("noreply@gawp.com");
        mail.setTo(applicant.getEmail());
        mail.setSubject("Size Spring ilen Salamlar getirmişem");
        mail.setContent("You have notified");
        emailService.sendSimpleMessage(mail);
        return "redirect:/grad/applicationsBeforeForwarding/preReview";

    }


    @RequestMapping("/grad/applicationsBeforeForwarding/declined")
    public String listDeclinedApplications(Model model){
        //TODO List declined applications and add it as attribute to model

        return "/grad/declined-applications";
    }

    @RequestMapping("/grad/applicationsBeforeForwarding/verifiedAndApproved")
    public String listVerifiedAndApprovedApplications(Model model){
        //TODO List verified and approved applications and add it as attribute to model
        return "/grad/verfied-and-approved-applications";
    }

    @RequestMapping("/grad/applicationsBeforeForwarding/verify")
    public String listApplicationsToVerify(Model model){
        //TODO List approved applications and add it as attribute to model
        return "/grad/verify-approved-applications";
    }

    //  File Mapping

    @RequestMapping("/docs/{id}")
    public ResponseEntity<Resource> serveDocument(@PathVariable long id){
        Document doc = documentService.getById(id);

        // TODO Check whether the user should be able to access document
        // TODO For example, an applicant shouldn't see someone elses document

        Resource document = storageService.loadAsResource(doc);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + document.getFilename() + "\"").body(document);

    }


}
