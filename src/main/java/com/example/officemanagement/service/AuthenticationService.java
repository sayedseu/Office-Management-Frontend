package com.example.officemanagement.service;

import com.example.officemanagement.model.AdminToken;
import com.example.officemanagement.model.UserToken;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AuthenticationService {
    @Value(value = "${backendBaseURl}/auth")
    private String authBaseUrl;

    public UserToken createUser(UserToken userToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<UserToken> userTokenHttpEntity = new HttpEntity<>(userToken);
        try {
            ResponseEntity<UserToken> userTokenResponseEntity = restTemplate.exchange(authBaseUrl + "/user/new",
                    HttpMethod.POST,
                    userTokenHttpEntity,
                    UserToken.class);
            return userTokenResponseEntity.getBody();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            return null;
        }
    }

    public AdminToken authenticateAdmin(String id, String password) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("id", id);
        multiValueMap.add("password", password);
        try {
            return restTemplate.postForObject(authBaseUrl + "/admin", multiValueMap, AdminToken.class);
        } catch (Exception e) {
            Notification.show("Login Failed", 3000, Notification.Position.TOP_CENTER);
            return null;
        }
    }

    public UserToken retrieveUserById(String id) {

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<UserToken> responseEntity = restTemplate.exchange(
                    authBaseUrl + "/user/" + id,
                    HttpMethod.GET,
                    null,
                    UserToken.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            return null;
        }
    }

    public List<UserToken> retrieve() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<List<UserToken>> responseEntity = restTemplate.exchange(
                    authBaseUrl + "/user/all"
                    , HttpMethod.GET
                    , null
                    , new ParameterizedTypeReference<List<UserToken>>() {
                    });
            return responseEntity.getBody();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            return null;
        }
    }

    public boolean deleteById(String id) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("id", id);
        try {
            return restTemplate.postForObject(authBaseUrl + "/user/delete", multiValueMap, Boolean.class);
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            return false;
        }
    }

}
