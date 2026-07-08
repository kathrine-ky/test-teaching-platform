package com.software.testing.discussionmodule.controller;

import com.software.testing.common.Result;
import com.software.testing.discussionmodule.service.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/discussion")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    /** 帖子列表 */
    @GetMapping("/posts")
    public Result<?> listPosts() {
        return discussionService.listPosts();
    }

    /** 帖子详情 + 评论 */
    @GetMapping("/post/{postId}")
    public Result<?> getPostDetail(@PathVariable Long postId) {
        return discussionService.getPostDetail(postId);
    }

    /** 发帖 */
    @PostMapping("/post")
    public Result<?> createPost(@RequestParam String title,
                                @RequestParam String content,
                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return discussionService.createPost(userId, title, content);
    }

    /** 删帖 */
    @DeleteMapping("/post/{postId}")
    public Result<?> deletePost(@PathVariable Long postId,
                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return discussionService.deletePost(postId, userId);
    }

    /** 点赞 */
    @PostMapping("/post/{postId}/like")
    public Result<?> likePost(@PathVariable Long postId) {
        return discussionService.likePost(postId);
    }

    /** 评论 */
    @PostMapping("/post/{postId}/comment")
    public Result<?> addComment(@PathVariable Long postId,
                                @RequestParam String content,
                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return discussionService.addComment(postId, userId, content);
    }

    /** 删除评论 */
    @DeleteMapping("/comment/{commentId}")
    public Result<?> deleteComment(@PathVariable Long commentId,
                                   HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return discussionService.deleteComment(commentId, userId);
    }
}
