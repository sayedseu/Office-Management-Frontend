package com.example.officemanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserToken {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String role;
}
