package com.hdu.language_learning_system.course.entity;


import com.hdu.language_learning_system.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "courses")
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    private String courseName;

    private String courseType; // 班级 or 1对1

    @Column(columnDefinition = "TEXT")
    private String courseContent;

    private String classGroupCode; // 班级编号（如T01），1对1为空

    @ManyToOne
    @JoinColumn(name = "student_id") // 1对1课程学员
    private User student;

    private Integer totalHours;

    private Integer remainingHours;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course that = (Course) o;
        return courseId != null && courseId.equals(that.courseId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(courseId);
    }
}