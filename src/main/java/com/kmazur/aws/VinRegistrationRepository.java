package com.kmazur.aws;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.stream.Stream;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

@Repository
public interface VinRegistrationRepository extends JpaRepository<VinRegistrationEntity, String> {

    @QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "1"))
    @Query("from vinregister")
    Stream<VinRegistrationEntity> getAllRecords();

}