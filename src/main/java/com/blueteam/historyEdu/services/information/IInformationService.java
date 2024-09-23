package com.blueteam.historyEdu.services.information;

import com.blueteam.historyEdu.dtos.InformationDTO;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.responses.CourseResponse;

public interface IInformationService {

    CourseResponse createInformation(Long lessonId, InformationDTO informationDTO) throws DataNotFoundException, PermissionDenyException;

    CourseResponse updateInformation(Long informationId, InformationDTO informationDTO) throws DataNotFoundException, PermissionDenyException;

    void deleteInformation(Long informationId) throws DataNotFoundException;
}
