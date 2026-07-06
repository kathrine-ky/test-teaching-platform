package com.software.testing.coursemodule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.software.testing.common.Result;
import com.software.testing.coursemodule.dto.CourseDTO;
import com.software.testing.coursemodule.entity.Course;
import com.software.testing.coursemodule.mapper.CourseMapper;
import com.software.testing.coursemodule.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Result<?> createCourse(CourseDTO courseDTO, Long teacherId) {
        log.info("[创建课程] courseName={}, teacherId={}", courseDTO.getCourseName(), teacherId);
        Course course = new Course();
        course.setCourseName(courseDTO.getCourseName());
        course.setDescription(courseDTO.getDescription());
        course.setTeacherId(teacherId);
        courseMapper.insert(course);
        log.info("[创建课程成功] id={}, name={}", course.getId(), course.getCourseName());
        return Result.success("创建成功", course);
    }

    @Override
    public Result<?> updateCourse(Long courseId, CourseDTO courseDTO) {
        log.info("[更新课程] id={}, name={}", courseId, courseDTO.getCourseName());
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            log.warn("[课程不存在] id={}", courseId);
            return Result.error("课程不存在");
        }
        course.setCourseName(courseDTO.getCourseName());
        course.setDescription(courseDTO.getDescription());
        courseMapper.updateById(course);
        log.info("[更新课程成功] id={}", courseId);
        return Result.success("更新成功", course);
    }

    @Override
    public Result<?> deleteCourse(Long courseId) {
        log.info("[删除课程] id={}", courseId);
        courseMapper.deleteById(courseId);
        log.info("[删除课程成功] id={}", courseId);
        return Result.success("删除成功", null);
    }

    @Override
    public Result<?> getCourseDetail(Long courseId) {
        log.info("[查询课程详情] id={}", courseId);
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            log.warn("[课程不存在] id={}", courseId);
            return Result.error("课程不存在");
        }
        return Result.success(course);
    }

    @Override
    public Result<?> listCourses(Long teacherId) {
        log.info("[查询课程列表] teacherId={}", teacherId);
        List<Course> courses = courseMapper.selectList(new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, teacherId)
                .orderByDesc(Course::getCreateTime));
        log.info("[查询课程列表结果] count={}", courses.size());
        return Result.success(courses);
    }
}
