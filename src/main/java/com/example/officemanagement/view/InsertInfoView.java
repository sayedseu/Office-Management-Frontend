package com.example.officemanagement.view;

import com.example.officemanagement.model.AdminToken;
import com.example.officemanagement.model.Information;
import com.example.officemanagement.model.UserToken;
import com.example.officemanagement.service.AuthenticationService;
import com.example.officemanagement.service.BackendService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Route(value = "admin/insert-info")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@Viewport("width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover")
public class InsertInfoView extends AppLayout {

    private AuthenticationService authenticationService;
    private BackendService backendService;
    private HttpSession httpSession;
    private Header header;
    private AdminToken adminToken;
    private VerticalLayout verticalLayout;
    private Tabs tabs;
    private Tab insertInfoTab;
    private Grid<UserToken> userTokenGrid;
    private List<UserToken> userTokenList;
    private FormLayout formLayout;
    private TextField id;

    public InsertInfoView(AuthenticationService authenticationService, BackendService backendService, HttpSession httpSession) {
        this.authenticationService = authenticationService;
        this.backendService = backendService;
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
        initGrid();
        initMainView();
        setContent(verticalLayout);

    }


    private void initLayout() {
        verticalLayout = new VerticalLayout();
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        verticalLayout.add(header);
    }

    private void setUpTab() {
        tabs = new Tabs();
        Tab creatUserTab = new Tab("Create User");
        insertInfoTab = new Tab("Insert Information");
        Tab logoutTab = new Tab("Log Out");
        tabs.add(creatUserTab, insertInfoTab, logoutTab);
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        addToDrawer(tabs);
        tabs.setSelectedTab(insertInfoTab);

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

        id = new TextField("Id");

        ComboBox<String> designationBox = new ComboBox<>("Designation");
        designationBox.setItems("Executive Officer", "Operating Officer", "Financial Officer", "Technology Officer", "Marketing Officer");
        designationBox.setValue("--Select--");

        ComboBox<String> locationBox = new ComboBox<>("Current Location");
        locationBox.setItems("Dhaka", "Rajshahi", "Shylet", "Chottogram", "Khulna", "Rangpur");
        locationBox.setValue("--Select--");

        DatePicker datePicker = new DatePicker();
        datePicker.setLabel("Office joining date");
        datePicker.setValue(LocalDate.now());

        TextArea responsibility = new TextArea("Duty and responsibility");
        responsibility.getStyle().set("minHeight", "100px");
        responsibility.setPlaceholder("Write here ...");

        TextArea task = new TextArea("Assigned task");
        task.getStyle().set("minHeight", "100px");
        task.setPlaceholder("Write here ...");

        Button uploadBT = new Button("UPLOAD", new Icon(VaadinIcon.UPLOAD));
        uploadBT.addClickShortcut(Key.ENTER);
        uploadBT.addThemeVariants(ButtonVariant.LUMO_LARGE,
                ButtonVariant.LUMO_PRIMARY);

        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));
        formLayout.add(id, designationBox, datePicker, task, responsibility, locationBox, uploadBT);
        formLayout.setColspan(designationBox, 2);
        formLayout.setColspan(datePicker, 2);
        formLayout.setColspan(task, 2);
        formLayout.setColspan(responsibility, 2);
        formLayout.setColspan(locationBox, 2);
        formLayout.setColspan(id, 2);
        formLayout.setColspan(uploadBT, 2);

        formLayout.setVisible(false);
        verticalLayout.add(formLayout);

        Binder<Information> binder = new Binder<>();
        binder.forField(id)
                .asRequired()
                .bind(Information::getId, Information::setId);
        binder.forField(datePicker)
                .asRequired()
                .bind(Information::getJoiningDate, Information::setJoiningDate);
        binder.forField(designationBox)
                .asRequired()
                .bind(Information::getDesignation, Information::setDesignation);
        binder.forField(locationBox)
                .asRequired()
                .bind(Information::getCurrentLocation, Information::setCurrentLocation);
        binder.forField(task)
                .asRequired()
                .bind(Information::getAssignedTask, Information::setAssignedTask);
        binder.forField(responsibility)
                .asRequired()
                .bind(Information::getResponsibility, Information::setResponsibility);

        uploadBT.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            Information information = new Information();
            try {
                binder.writeBean(information);
                Information info = backendService.createInfo(information);
                if (info != null) {
                    Notification.show("Upload Successful", 5000, Notification.Position.TOP_CENTER);
                    binder.setBean(null);
                    id.clear();
                    datePicker.setValue(LocalDate.now());
                    task.clear();
                    designationBox.setValue("--Select--");
                    locationBox.setValue("--Select--");
                    responsibility.clear();
                    formLayout.setVisible(false);
                }

            } catch (ValidationException e) {
                Notification.show(e.getMessage());
            }
        });
    }

    private void initGrid() {
        userTokenGrid = new Grid<>(UserToken.class);
        userTokenGrid.setHeightByRows(true);
        userTokenList = authenticationService.retrieve();
        if (userTokenList.size() != 0) {
            userTokenGrid.setItems(userTokenList);
            userTokenGrid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS
                    , GridVariant.LUMO_ROW_STRIPES);
            userTokenGrid.removeColumnByKey("role");
            userTokenGrid.removeColumnByKey("password");
        }

        userTokenGrid.addItemDoubleClickListener(userTokenItemDoubleClickEvent -> {
            formLayout.setVisible(true);
            UserToken item = userTokenItemDoubleClickEvent.getItem();
            id.setValue(item.getId());
        });

        verticalLayout.add(userTokenGrid);
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
            tabs.setSelectedTab(insertInfoTab);
            dialog.close();
        });
        dialog.add(messageLabel, confirmButton, cancelButton);
        dialog.setWidth("140px");
        dialog.setHeight("140px");
        dialog.open();
    }

}
