package org.crm.student.application_management_service.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.crm.student.application_management_service.service.LocalDateTimeConverter;

import java.time.LocalDateTime;

@Data
public class CandidateCSV {

    @CsvBindByName(column = "firstName")
    private String firstName;

    @CsvBindByName(column = "lastName")
    private String lastName;

    @CsvBindByName(column = "country")
    private String country;

    @CsvBindByName(column = "city")
    private String city;

    @CsvBindByName(column = "email")
    private String email;

    @CsvBindByName(column = "phoneNumber")
    private String phoneNumber;

    @CsvBindByName(column = "applicationSource")
    private String applicationSource;

    @CsvBindByName(column = "field")
    private String field;

    @CsvCustomBindByName(column = "applicationDate", converter = LocalDateTimeConverter.class)
    private LocalDateTime applicationDate;

    // Parent details
    @CsvBindByName(column = "parentFullName")
    private String parentFullName;

    @CsvBindByName(column = "parentEmail")
    private String parentEmail;

    @CsvBindByName(column = "parentPhone")
    private String parentPhone;

    @CsvBindByName(column = "parentRelationship")
    private String parentRelationship;


    // Employment details
    @CsvBindByName(column = "companyName")
    private String companyName;

    @CsvBindByName(column = "responsibilities")
    private String responsibilities;

    // Education details
    @CsvBindByName(column = "highestEducation")
    private String highestEducation;

    @CsvBindByName(column = "institutionName")
    private String institutionName;

    @CsvBindByName(column = "graduationYear")
    private int graduationYear;

    @CsvBindByName(column = "fieldOfStudy")
    private String fieldOfStudy;



    private Status parseStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase()); // Convert to upper case to match enum values
        } catch (IllegalArgumentException e) {
            // Handle the case where the status is invalid
            System.err.println("Invalid status: " + status + ". Defaulting to NEW.");
            return Status.NEW; // Default value
        }
    }

}
