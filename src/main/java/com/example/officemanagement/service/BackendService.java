package com.example.officemanagement.service;

import com.example.officemanagement.model.Information;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class BackendService {
    @Value(value = "${backendBaseURl}/info")
    private String infoBaseUrl;

    public Information createInfo(Information information) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Information> userTokenHttpEntity = new HttpEntity<>(information);
        try {
            ResponseEntity<Information> userTokenResponseEntity = restTemplate.exchange(infoBaseUrl + "/new",
                    HttpMethod.POST,
                    userTokenHttpEntity,
                    Information.class);
            return userTokenResponseEntity.getBody();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            return null;
        }
    }

    public Information retrieveInfoById(String id) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Information> responseEntity = restTemplate.exchange(
                    infoBaseUrl + "/" + id,
                    HttpMethod.GET,
                    null,
                    Information.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            return null;
        }
    }

    public List<Information> retrieve() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<List<Information>> responseEntity = restTemplate.exchange(
                    infoBaseUrl + "/all"
                    , HttpMethod.GET
                    , null
                    , new ParameterizedTypeReference<List<Information>>() {
                    });
            return responseEntity.getBody();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            return null;
        }
    }

}
