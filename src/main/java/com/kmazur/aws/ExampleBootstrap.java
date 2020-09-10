package com.kmazur.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ExampleBootstrap {

    @Autowired
    private VinRegistrationRepository registrationRepository;

    @PostConstruct
    public void mockSampleData() {
         VinRegistrationEntity entity1 = new VinRegistrationEntity();
         entity1.setRegistrationNumber("001");
         VinRegistrationEntity entity2 = new VinRegistrationEntity();
         entity2.setRegistrationNumber("002");
        registrationRepository.save(entity1);
        registrationRepository.save(entity2);
    }
}
