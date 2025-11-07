package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FAQRequest {
    private String category;
    private String question;
    private String answer;
    private Integer displayOrder;
}
