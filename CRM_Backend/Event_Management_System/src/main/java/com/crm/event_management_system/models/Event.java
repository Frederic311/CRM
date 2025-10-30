package com.crm.event_management_system.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="event_name",nullable = false, unique = true)
    private String name;

    @Column(name="start_date", nullable=false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate start;

    @Column(name="end_date", nullable=false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate end;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name="start_time", nullable=false)
    private LocalTime start_time;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name="end_time", nullable=false)
    private LocalTime end_time;

    @Column(name="expected_person", nullable=false)
    private int expected_person;

    @Column(name="budget", nullable=false)
    private float budget;

    @Column(name="description", nullable=false)
    private String description;

    @Column(name="type_id")
    private Long typeId;

    @Column(name="venue")
    private String venue;

}
