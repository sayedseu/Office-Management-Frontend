package com.example.officemanagement.view;

import com.example.officemanagement.model.AdminToken;
import com.example.officemanagement.service.AuthenticationService;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import javax.servlet.http.HttpSession;

@Route(value = "")
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class LoginView extends VerticalLayout {

    private AuthenticationService authenticationService;
    private HttpSession httpSession;
    private LoginForm loginComponent;
    private AdminToken adminToken;

    public LoginView(AuthenticationService authenticationService, HttpSession httpSession) {
        super();

        this.authenticationService = authenticationService;
        this.httpSession = httpSession;

        loginComponent = new LoginForm();
        loginComponent.addLoginListener(e -> {
            boolean isAuthenticated = authenticate(e);
            if (isAuthenticated) {
                navigateToMainPage(e);
            } else {
                loginComponent.setError(true);
            }
        });
        setAlignItems(Alignment.CENTER);
        add(loginComponent);
    }

    private void navigateToMainPage(AbstractLogin.LoginEvent e) {

        if (adminToken.getRole().equals("admin")) {
            httpSession.setAttribute("admin", adminToken);
            loginComponent.getUI().ifPresent(ui -> ui.navigate("admin/create-user"));
        }

    }

    private boolean authenticate(AbstractLogin.LoginEvent e) {
        adminToken = authenticationService.authenticateAdmin(e.getUsername(), e.getPassword());
        return adminToken != null && adminToken.getRole().equals("admin");
    }
}