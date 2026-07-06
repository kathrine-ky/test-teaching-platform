package com.software.testing.coursemodule.controller;

import com.software.testing.common.Result;
import com.software.testing.coursemodule.dto.CourseDTO;
import com.software.testing.coursemodule.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/create")
    public Result<?> create(@Valid @RequestBody CourseDTO courseDTO, HttpServletRequest request) {
        Long teacherId = (Long) request.getAttribute("userId");
        return courseService.createCourse(courseDTO, teacherId);
    }

    @PutMapping("/update/{courseId}")
    public Result<?> update(@PathVariable Long courseId, @Valid @RequestBody CourseDTO courseDTO) {
        return courseService.updateCourse(courseId, courseDTO);
    }

    @DeleteMapping("/{courseId}")
    public Result<?> delete(@PathVariable Long courseId) {
        return courseService.deleteCourse(courseId);
    }

    @GetMapping("/{courseId}")
    public Result<?> detail(@PathVariable Long courseId) {
        return courseService.getCourseDetail(courseId);
    }

    @GetMapping("/list")
    public Result<?> list(HttpServletRequest request) {
        Long teacherId = (Long) request.getAttribute("userId");
        return courseService.listCourses(teacherId);
    }
}
