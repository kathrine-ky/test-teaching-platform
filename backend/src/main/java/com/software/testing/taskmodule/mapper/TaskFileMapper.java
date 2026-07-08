package com.software.testing.taskmodule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.software.testing.taskmodule.entity.TaskFile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskFileMapper extends BaseMapper<TaskFile> {
}
