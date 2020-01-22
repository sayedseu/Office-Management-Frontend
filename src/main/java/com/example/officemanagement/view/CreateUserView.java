package com.example.officemanagement.view;

import com.example.officemanagement.model.AdminToken;
import com.example.officemanagement.model.UserToken;
import com.example.officemanagement.service.AuthenticationService;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import javax.servlet.http.HttpSession;

@Route(value = "admin/create-user")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@Viewport("width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover")
public class CreateUserView extends AppLayout {

    private AuthenticationService service;
    private HttpSession httpSession;
    private Header header;
    private AdminToken adminToken;
    private VerticalLayout verticalLayout;
    private Tabs tabs;
    private Tab creatUserTab;

    public CreateUserView(AuthenticationService service, HttpSession httpSession) {
        this.service = service;
        this.httpSession = httpSession;

        header = new Header(httpSession);
        header.addAttachListener(attachEvent -> {
            AdminToken adminToken = header.getAdminToken();
            if (adminToken.getId() != null) {
                this.adminToken = adminToken;
            }

            if (!adminToken.getRole().equals("admin")) {
                httpSession.removeAttribute("admin");
                header.getUI().ifPresent(ui -> ui.navigate(""));
            }
        });

        setUpTab();
        initLayout();
        initMainView();
        setContent(verticalLayout);
    }

    private void initLayout() {
        verticalLayout = new VerticalLayout();
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        verticalLayout.setMargin(true);
        verticalLayout.add(header);
    }

    private void setUpTab() {
        tabs = new Tabs();
        creatUserTab = new Tab("Create User");
        Tab insertInfoTab = new Tab("Insert Information");
        Tab logoutTab = new Tab("Log Out");
        tabs.add(creatUserTab, insertInfoTab, logoutTab);
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        addToDrawer(tabs);
        tabs.setSelectedTab(creatUserTab);

        tabs.addSelectedChangeListener((ComponentEventListener<Tabs.SelectedChangeEvent>) selectedChangeEvent -> {
            switch (selectedChangeEvent.getSelectedTab().getLabel()) {
                case "Create User":
                    creatUserTab.getUI().ifPresent(ui -> ui.navigate("admin/create-user"));
                    break;
                case "Insert Information":
                    creatUserTab.getUI().ifPresent(ui -> ui.navigate("admin/insert-info"));
                    break;
                case "Log Out":
                    logout();
                    break;
            }
        });
    }

    private void initMainView() {

        TextField id = new TextField("Id");
        PasswordField password = new PasswordField("Password");
        EmailField email = new EmailField("Email");
        email.setClearButtonVisible(true);
        email.setErrorMessage("Please enter a valid email address");
        TextField phone = new TextField("Phone Number");
        TextField name = new TextField("Name");
        Button addBT = new Button("ADD", new Icon(VaadinIcon.USER));
        addBT.addClickShortcut(Key.ENTER);
        addBT.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_PRIMARY);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));


        formLayout.add(id, name, email, phone, password, addBT);
        formLayout.setColspan(id, 2);
        formLayout.setColspan(password, 2);
        formLayout.setColspan(name, 2);
        formLayout.setColspan(email, 2);
        formLayout.setColspan(phone, 2);
        formLayout.setColspan(addBT, 2);
        verticalLayout.add(formLayout);

        Binder<UserToken> binder = new Binder<>();
        binder.forField(id)
                .asRequired()
                .bind(UserToken::getId, UserToken::setId);
        binder.forField(name)
                .asRequired()
                .withValidator(n -> n.length() >= 3, "Names should be at least 3 letters long")
                .withValidator(n -> n.length() <= 30, "Names cannot be more than 10 letters long")
                .withValidator(n -> !n.contains("_"), "Names cannot have underscores")
                .bind(UserToken::getName, UserToken::setName);
        binder.forField(email)
                .asRequired()
                .withValidator(new EmailValidator("Please enter a valid email address"))
                .bind(UserToken::getEmail, UserToken::setEmail);
        binder.forField(phone)
                .asRequired()
                .withValidator(n -> n.length() >= 11, "Number should be at least 11 letters long")
                .withValidator(n -> n.length() <= 11, "Number cannot be more than 11 letters long")
                .bind(UserToken::getPhone, UserToken::setPhone);
        binder.forField(password)
                .asRequired()
                .bind(UserToken::getPassword, UserToken::setPassword);

        addBT.addClickListener(buttonClickEvent -> {

            UserToken userToken = new UserToken();
            try {
                binder.writeBean(userToken);
                userToken.setRole("user");
                UserToken user = service.createUser(userToken);
                if (user != null) {
                    Notification.show("Saving Successful " + user.getName(), 5000, Notification.Position.TOP_CENTER);
                    binder.setBean(null);
                    id.clear();
                    name.clear();
                    email.clear();
                    phone.clear();
                    password.clear();
                }
            } catch (ValidationException e) {
                Notification.show(e.getMessage());
            }

        });
    }

    private void logout() {

        Dialog dialog = new Dialog();

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        Label messageLabel = new Label("Are you sure you want to Sign Out?");

        NativeButton confirmButton = new NativeButton("Confirm", event -> {
            httpSession.removeAttribute("admin");
            dialog.getUI().ifPresent(ui -> ui.navigate(""));
            dialog.close();
        });
        NativeButton cancelButton = new NativeButton("Cancel", event -> {
            tabs.setSelectedTab(creatUserTab);
            dialog.close();
        });
        dialog.add(messageLabel, confirmButton, cancelButton);
        dialog.setWidth("140px");
        dialog.setHeight("140px");
        dialog.open();
    }
}
