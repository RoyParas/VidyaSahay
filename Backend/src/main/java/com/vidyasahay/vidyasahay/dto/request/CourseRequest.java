package com.vidyasahay.vidyasahay.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CourseRequest {

    private UUID instituteId;

    private UUID professionId;

    @Size(
            max = 200,
            message = "Course name cannot exceed 200 characters"
    )
    private String name;

    @Positive(
            message = "Duration years must be greater than zero"
    )
    private Integer durationYears;

    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Course fees cannot be negative"
    )
    private BigDecimal fees;

    public CourseRequest() {
    }

    public UUID getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(UUID instituteId) {
        this.instituteId = instituteId;
    }

    public UUID getProfessionId() {
        return professionId;
    }

    public void setProfessionId(UUID professionId) {
        this.professionId = professionId;
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