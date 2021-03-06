package com.gawpdevelopers.gawp.converters;

import com.gawpdevelopers.gawp.commands.ApplicationForm;
import com.gawpdevelopers.gawp.domain.Application;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ApplicationToApplicationForm implements Converter<Application, ApplicationForm> {

    @Override
    public ApplicationForm convert(Application application) {
        ApplicationForm applicationForm = new ApplicationForm();
        if (applicationForm.getId() != null  && !StringUtils.isEmpty(application.getId())) {
            applicationForm.setId(new Long(application.getId()));
        }
        applicationForm.setId(application.getId());
        //applicationForm.setAdvert(application.getAdvert());
        applicationForm.setInterview(application.getInterview());
        applicationForm.setApplicant(application.getApplicant());
        applicationForm.setStatus(application.getStatus());
        applicationForm.setLastUpdateDate(application.getLastUpdateDate());

        return applicationForm;
    }
}
