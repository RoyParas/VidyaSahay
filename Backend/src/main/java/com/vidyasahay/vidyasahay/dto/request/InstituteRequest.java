package com.vidyasahay.vidyasahay.dto.request;



public record InstituteRequest(
     String instituteName,
     String country, 
     String state,
     String district,
     String city,
     String location,
     int pincode,
     String status
) {}