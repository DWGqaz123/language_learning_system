package com.hdu.language_learning_system.task.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskAssignmentId implements Serializable {
    private Integer task;
    private Integer student;
}
