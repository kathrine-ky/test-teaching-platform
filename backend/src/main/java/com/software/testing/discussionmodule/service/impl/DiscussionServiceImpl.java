package com.software.testing.discussionmodule.service.impl;

import com.software.testing.common.Result;
import com.software.testing.discussionmodule.entity.DiscussionComment;
import com.software.testing.discussionmodule.entity.DiscussionPost;
import com.software.testing.discussionmodule.mapper.DiscussionCommentMapper;
import com.software.testing.discussionmodule.mapper.DiscussionPostMapper;
import com.software.testing.discussionmodule.service.DiscussionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DiscussionServiceImpl implements DiscussionService {

    private static final Logger log = LoggerFactory.getLogger(DiscussionServiceImpl.class);

    @Autowired
    private DiscussionPostMapper postMapper;
    @Autowired
    private DiscussionCommentMapper commentMapper;

    @Override
    public Result<?> listPosts() {
        List<Map<String, Object>> posts = postMapper.selectAllWithUser();
        return Result.success(posts);
    }

    @Override
    public Result<?> getPostDetail(Long postId) {
        Map<String, Object> post = postMapper.selectWithUserById(postId);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        // 增加浏览量
        postMapper.incrementViewCount(postId);
        List<Map<String, Object>> comments = commentMapper.selectWithUserByPostId(postId);
        Map<String, Object> result = new HashMap<>();
        result.put("post", post);
        result.put("comments", comments);
        return Result.success(result);
    }

    @Override
    @Transactional
    public Result<?> createPost(Long userId, String title, String content) {
        DiscussionPost post = new DiscussionPost();
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent(content);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        postMapper.insert(post);
        log.info("[发帖成功] postId={}, userId={}", post.getId(), userId);
        return Result.success("发帖成功", post);
    }

    @Override
    @Transactional
    public Result<?> deletePost(Long postId, Long userId) {
        DiscussionPost post = postMapper.selectById(postId);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        if (!post.getUserId().equals(userId)) {
            return Result.error("只能删除自己的帖子");
        }
        // 删除帖子下的所有评论
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("post_id", postId);
        commentMapper.deleteByMap(wrapper);
        postMapper.deleteById(postId);
        log.info("[删帖成功] postId={}, userId={}", postId, userId);
        return Result.success("删除成功", null);
    }

    @Override
    public Result<?> likePost(Long postId) {
        postMapper.incrementLikeCount(postId);
        DiscussionPost post = postMapper.selectById(postId);
        return Result.success("点赞成功", post != null ? post.getLikeCount() : 0);
    }

    @Override
    @Transactional
    public Result<?> addComment(Long postId, Long userId, String content) {
        DiscussionPost post = postMapper.selectById(postId);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        DiscussionComment comment = new DiscussionComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        commentMapper.insert(comment);
        postMapper.incrementCommentCount(postId);
        log.info("[评论成功] commentId={}, postId={}, userId={}", comment.getId(), postId, userId);
        return Result.success("评论成功", comment);
    }

    @Override
    @Transactional
    public Result<?> deleteComment(Long commentId, Long userId) {
        DiscussionComment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        DiscussionPost post = postMapper.selectById(comment.getPostId());
        // 发帖人可以删除任何评论，评论人只能删除自己的评论
        boolean isPostOwner = post != null && post.getUserId().equals(userId);
        boolean isCommentOwner = comment.getUserId().equals(userId);
        if (!isPostOwner && !isCommentOwner) {
            return Result.error("无权删除此评论");
        }
        commentMapper.deleteById(commentId);
        postMapper.decrementCommentCount(comment.getPostId());
        log.info("[删除评论成功] commentId={}, userId={}, isPostOwner={}", commentId, userId, isPostOwner);
        return Result.success("删除成功", null);
    }
}
