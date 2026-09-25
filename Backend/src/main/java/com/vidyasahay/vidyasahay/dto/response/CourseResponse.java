package com.vidyasahay.vidyasahay.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class CourseResponse {

    private UUID id;

    private UUID instituteId;

    private String instituteName;

    private UUID professionId;

    private String professionName;

    private String name;

    private Integer durationYears;

    private BigDecimal fees;

    public CourseResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(UUID instituteId) {
        this.instituteId = instituteId;
    }

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public UUID getProfessionId() {
        return professionId;
    }

    public void setProfessionId(UUID professionId) {
        this.professionId = professionId;
    }

    public String getProfessionName() {
        return professionName;
    }

    public void setProfessionName(String professionName) {
        this.professionName = professionName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDurationYears() {
        return durationYears;
    }

    public void setDurationYears(Integer durationYears) {
        this.durationYears = durationYears;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }
}