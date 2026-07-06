package com.software.testing.coursemodule.service;

import com.software.testing.common.Result;
import com.software.testing.coursemodule.dto.CourseDTO;

public interface CourseService {
    Result<?> createCourse(CourseDTO courseDTO, Long teacherId);
    Result<?> updateCourse(Long courseId, CourseDTO courseDTO);
    Result<?> deleteCourse(Long courseId);
    Result<?> getCourseDetail(Long courseId);
    Result<?> listCourses(Long teacherId);
}
