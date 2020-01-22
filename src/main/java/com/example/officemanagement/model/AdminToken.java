package com.example.officemanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminToken {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String role;

    public AdminToken() {
        role = "norole";
    }
}