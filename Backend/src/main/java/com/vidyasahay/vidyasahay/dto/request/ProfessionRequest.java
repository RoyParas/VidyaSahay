package com.vidyasahay.vidyasahay.dto.request;

import jakarta.validation.constraints.Size;

public class ProfessionRequest {

    @Size(
            max = 150,
            message = "Profession name cannot exceed 150 characters"
    )
    private String name;

    public ProfessionRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}