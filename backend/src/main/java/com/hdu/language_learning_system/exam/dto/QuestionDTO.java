package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionDTO implements Serializable {
    private String questionId;
    private String questionText;
    private String type; // objective / subjective
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private Integer score;
}