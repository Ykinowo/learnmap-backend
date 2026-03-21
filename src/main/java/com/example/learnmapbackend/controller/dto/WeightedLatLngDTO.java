package com.example.learnmapbackend.controller.dto;

import lombok.Data;

@Data
public class WeightedLatLngDTO {
    private double lat;
    private double lng;
    private double weight;
}