package org.crm.student.application_management_service.model;




import jakarta.persistence.*;

import lombok.Data;


@Entity
@Table(name = "education_details")
@Data
public class EducationDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String highestEducation;

    private String institutionName;

    private int graduationYear;

    private String fieldOfStudy;
}


