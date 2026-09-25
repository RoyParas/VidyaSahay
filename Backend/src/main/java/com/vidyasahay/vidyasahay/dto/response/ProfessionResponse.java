package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

public class ProfessionResponse {

    private UUID id;

    private String name;

    public ProfessionResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}