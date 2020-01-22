package com.example.officemanagement.view;

import com.example.officemanagement.model.AdminToken;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import javax.servlet.http.HttpSession;

public class Header extends HorizontalLayout {
    private AdminToken adminToken;

    public Header(HttpSession httpSession) {
        super();
        adminToken = (AdminToken) httpSession.getAttribute("admin");
        if (adminToken == null) {
            adminToken = new AdminToken();
        }
    }

    public AdminToken getAdminToken() {
        return adminToken;
    }
}