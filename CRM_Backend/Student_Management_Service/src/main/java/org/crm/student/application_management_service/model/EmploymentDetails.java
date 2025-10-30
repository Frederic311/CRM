package org.crm.student.application_management_service.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;




@Entity
@Table(name = "employment_details")
@Data
public class EmploymentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String companyName;

    private String responsibilities;
}



