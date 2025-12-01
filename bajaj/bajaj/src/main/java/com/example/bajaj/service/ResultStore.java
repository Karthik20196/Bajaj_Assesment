package com.example.bajaj.service;

import org.springframework.stereotype.Component;

@Component
public class ResultStore {

    private String finalQuery;

    public String getFinalQuery() {
        return finalQuery;
    }

    public void setFinalQuery(String finalQuery) {
        this.finalQuery = finalQuery;
    }
}
