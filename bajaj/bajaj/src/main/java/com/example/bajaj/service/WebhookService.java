package com.example.bajaj.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate;
    private final SqlSolverService sqlSolverService;
    private final ResultStore resultStore;

    @Value("${bajaj.generate.url}")
    private String generatorUrl;

    public WebhookService(RestTemplate restTemplate,
                          SqlSolverService sqlSolverService,
                          ResultStore resultStore) {
        this.restTemplate = restTemplate;
        this.sqlSolverService = sqlSolverService;
        this.resultStore = resultStore;
    }

    public void runFlow() {

        // ------- STEP 1: Generate Webhook -------
        Map<String, String> body = new HashMap<>();
        body.put("name", "Tailor Mohammad Afnan");
        body.put("regNo", "22BCE9906");
        body.put("email", "afnan.22bce9906@vitapstudent.ac.in");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(generatorUrl, request, Map.class);

        Map<String, Object> resp = response.getBody();

        System.out.println("FULL RESPONSE = " + resp);

        if (resp == null || !resp.containsKey("webhook")) {
            System.out.println("Invalid response from server!");
            return;
        }

        String webhookUrl = (String) resp.get("webhook");
        String accessToken = (String) resp.get("accessToken");

        System.out.println("Webhook URL = " + webhookUrl);
        System.out.println("Access Token = " + accessToken);

        // ------- STEP 2: Generate SQL Query -------
        String finalQuery = sqlSolverService.buildFinalQuery();
        resultStore.setFinalQuery(finalQuery);

        // ------- STEP 3: Submit SQL to webhook -------

        HttpHeaders submitHeaders = new HttpHeaders();
        submitHeaders.setContentType(MediaType.APPLICATION_JSON);
        submitHeaders.setBearerAuth(accessToken);

        // Clean JSON with escaped quotes
        String safeQuery = finalQuery.replace("\"", "\\\"");
        String json = "{\"finalQuery\":\"" + safeQuery + "\"}";

        HttpEntity<String> submitReq = new HttpEntity<>(json, submitHeaders);

        try {
            ResponseEntity<String> submitResp =
                    restTemplate.postForEntity(webhookUrl, submitReq, String.class);

            System.out.println("SUBMIT STATUS = " + submitResp.getStatusCode());
            System.out.println("SUBMIT BODY = " + submitResp.getBody());

        } catch (Exception e) {
            System.out.println("Failed to submit: " + e.getMessage());
        }
    }
}
