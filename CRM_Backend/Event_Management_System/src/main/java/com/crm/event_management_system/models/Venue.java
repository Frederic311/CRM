package com.crm.event_management_system.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "venues")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venue_name", unique = true)
    private String name;

    @Column(name = "venue_address", nullable = false)
    private String address;

    @Column(name="capacity",nullable=false)
    private int capacity;

    @Column(name="latitude",nullable=false)
    private float latitude;

    @Column(name="longitude",nullable=false)
    private float longitude;

}
