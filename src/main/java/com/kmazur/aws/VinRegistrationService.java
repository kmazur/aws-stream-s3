package com.kmazur.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Service
public class VinRegistrationService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private VinRegistrationRepository vinRegistrationRepository;

    @Autowired
    private ServletContext context;

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Value("${csv.localTargetPath}")
    private String localTargetPath;


    @Async
    public void uploadingCSVFileToS3Bucket(String filePath) {
        try {
            boolean doesFileExist = Files.exists(Paths.get(filePath));
            System.out.println("doesFileExist = " + doesFileExist);
            Path path = Paths.get(filePath);
            File file = path.toFile();
            uploadFileToS3Bucket(bucketName, file);
            file.delete(); // To remove the file locally created in the project folder. This part will be done later
        } catch (final AmazonServiceException ex) {
            ex.printStackTrace();
        }
    }

    private void uploadFileToS3Bucket(final String bucketName, final File file) {
        final String uniqueFileName = LocalDateTime.now() + "_" + file.getName();
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
        amazonS3.putObject(putObjectRequest);
    }

    @Transactional(readOnly = true)
    public void getAllVinRegistrationDetailsInCSV(HttpServletResponse response) throws Exception {
        String fileName = "vgi-vin-registration-details.csv";
        String filePath = System.getProperty("user.home") + File.separator + fileName;
        String localFilePath = localTargetPath + File.separator + fileName;

        File localFile = new File(filePath);
        localFile.createNewFile();
        String contentType = context.getMimeType(localFile.getName());
        if (contentType == null) {
            contentType = "text/csv";
        }

        response.setContentType(contentType);
        response.addHeader(HttpHeaders.CONTENT_TYPE, "text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + localFile + "\"");
        try (
            Stream<VinRegistrationEntity> vinRegistrationDetailsStream = vinRegistrationRepository.getAllRecords();
            BufferedWriter fileWriter = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8);
            BufferedWriter localWriter = Files.newBufferedWriter(Paths.get(localFilePath), StandardCharsets.UTF_8)
        ) {
            PrintWriter out = response.getWriter();

            vinRegistrationDetailsStream
                .forEach(detail -> {
                    System.out.println(detail);
                    try {
                        String csvRow = toCsvRow(detail);
                        fileWriter.write(csvRow);
                        fileWriter.write("\n");
                        localWriter.write(csvRow);
                        localWriter.write("\n");

                        out.write(csvRow);
                        out.write("\n");
                        entityManager.detach(detail);//This is to make sure that GC in java cleans us from time to time so that there is no out of memory or any stack track exception
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });

            out.flush();
        } catch (IOException ix) {
            throw new RuntimeException("There is an error while downloading user_details.csv", ix);
        }
    }

    private String toCsvRow(VinRegistrationEntity detail) {
        // TODO: write here serialization logic for VinRegistrationEntity to CSV row - comma separated values row
        return String.format("%d,%s", detail.getId(), detail.getRegistrationNumber());
    }
}

