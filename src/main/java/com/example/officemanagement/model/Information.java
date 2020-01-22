package com.example.officemanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Information {
    private String id;
    private LocalDate joiningDate;
    private String designation;
    private String responsibility;
    private String assignedTask;
    private String currentLocation;
}
