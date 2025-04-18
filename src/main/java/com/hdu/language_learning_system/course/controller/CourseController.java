package com.hdu.language_learning_system.course.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.course.dto.CourseCreateDTO;
import com.hdu.language_learning_system.course.dto.AddStudentsToCourseDTO;
import com.hdu.language_learning_system.course.dto.GenerateStudentRecordsDTO;
import com.hdu.language_learning_system.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // 创建课程
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createCourse(@RequestBody CourseCreateDTO dto) {
        courseService.createCourse(dto);
        return ResponseEntity.ok(ApiResponse.success("课程创建成功", null));
    }

    //添加新学员到班级
    @PostMapping("/add-students-to-class")
    public ResponseEntity<ApiResponse<Void>> addStudentsToClassCourse(@RequestBody AddStudentsToCourseDTO dto) {
        courseService.addStudentsToClassCourse(dto);
        return ResponseEntity.ok(ApiResponse.success("班级学员添加成功", null));
    }
    //创建学员上课记录
    @PostMapping("/student-records/generate")
    public ResponseEntity<String> generateStudentScheduleRecords(@RequestBody GenerateStudentRecordsDTO dto) {
        try {
            courseService.generateStudentRecords(dto.getScheduleId());
            return ResponseEntity.ok("学生课程记录生成成功");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("生成失败：" + e.getMessage());
        }
    }

}
