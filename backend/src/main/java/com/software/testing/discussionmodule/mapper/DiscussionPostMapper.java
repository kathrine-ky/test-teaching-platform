package com.software.testing.discussionmodule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.software.testing.discussionmodule.entity.DiscussionPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface DiscussionPostMapper extends BaseMapper<DiscussionPost> {

    @Select("SELECT p.id, p.title, p.content, p.view_count as viewCount, p.like_count as likeCount, " +
            "p.comment_count as commentCount, p.create_time as createTime, " +
            "u.id as userId, u.real_name as userName, u.role as userRole " +
            "FROM t_discussion_post p LEFT JOIN t_user u ON p.user_id = u.id " +
            "ORDER BY p.create_time DESC")
    List<Map<String, Object>> selectAllWithUser();

    @Select("SELECT p.id, p.title, p.content, p.view_count as viewCount, p.like_count as likeCount, " +
            "p.comment_count as commentCount, p.create_time as createTime, " +
            "u.id as userId, u.real_name as userName, u.role as userRole " +
            "FROM t_discussion_post p LEFT JOIN t_user u ON p.user_id = u.id " +
            "WHERE p.id = #{postId}")
    Map<String, Object> selectWithUserById(@Param("postId") Long postId);

    @Update("UPDATE t_discussion_post SET view_count = view_count + 1 WHERE id = #{postId}")
    void incrementViewCount(@Param("postId") Long postId);

    @Update("UPDATE t_discussion_post SET like_count = like_count + 1 WHERE id = #{postId}")
    void incrementLikeCount(@Param("postId") Long postId);

    @Update("UPDATE t_discussion_post SET comment_count = comment_count + 1 WHERE id = #{postId}")
    void incrementCommentCount(@Param("postId") Long postId);

    @Update("UPDATE t_discussion_post SET comment_count = GREATEST(comment_count - 1, 0) WHERE id = #{postId}")
    void decrementCommentCount(@Param("postId") Long postId);
}
