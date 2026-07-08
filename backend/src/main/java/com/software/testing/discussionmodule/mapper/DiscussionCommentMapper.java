package com.software.testing.discussionmodule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.software.testing.discussionmodule.entity.DiscussionComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DiscussionCommentMapper extends BaseMapper<DiscussionComment> {

    @Select("SELECT c.id, c.post_id as postId, c.content, c.create_time as createTime, " +
            "u.id as userId, u.real_name as userName, u.role as userRole " +
            "FROM t_discussion_comment c LEFT JOIN t_user u ON c.user_id = u.id " +
            "WHERE c.post_id = #{postId} " +
            "ORDER BY c.create_time ASC")
    List<Map<String, Object>> selectWithUserByPostId(@Param("postId") Long postId);
}
