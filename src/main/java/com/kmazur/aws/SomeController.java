package com.kmazur.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class SomeController {

    @Autowired
    private VinRegistrationService vinRegistrationService;

    @GetMapping(value = "/getAllVinRegDetails/csv")
    @Transactional(readOnly = true)
    public void getAllVinRegistrationDetailsInCSV(HttpServletResponse response) throws Exception {
        vinRegistrationService.getAllVinRegistrationDetailsInCSV(response);
        String fileName = System.getProperty("user.home") + System.getProperty("file.separator")
            + "vgi-vin-registration-details.csv";//Path where csv file is stored, this is the blocker
        vinRegistrationService.uploadingCSVFileToS3Bucket(fileName);
    }

}
