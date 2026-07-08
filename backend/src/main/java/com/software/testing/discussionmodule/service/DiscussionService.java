package com.software.testing.discussionmodule.service;

import com.software.testing.common.Result;

public interface DiscussionService {
    /** 获取所有帖子列表 */
    Result<?> listPosts();
    /** 获取帖子详情（含评论），自动增加浏览量 */
    Result<?> getPostDetail(Long postId);
    /** 发帖 */
    Result<?> createPost(Long userId, String title, String content);
    /** 删帖（仅发帖人可删除） */
    Result<?> deletePost(Long postId, Long userId);
    /** 点赞帖子 */
    Result<?> likePost(Long postId);
    /** 发表评论 */
    Result<?> addComment(Long postId, Long userId, String content);
    /** 删除评论（发帖人可删任何评论，评论人只能删自己的） */
    Result<?> deleteComment(Long commentId, Long userId);
}
