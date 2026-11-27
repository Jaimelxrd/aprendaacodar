package mz.co.aprendaacodar.blog.controller;

import jakarta.validation.Valid;
import mz.co.aprendaacodar.blog.dto.BlogDtos.CommentRequest;
import mz.co.aprendaacodar.blog.dto.BlogDtos.CommentResponse;
import mz.co.aprendaacodar.blog.dto.BlogDtos.PostRequest;
import mz.co.aprendaacodar.blog.dto.BlogDtos.PostResponse;
import mz.co.aprendaacodar.blog.service.BlogService;
import mz.co.aprendaacodar.common.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class BlogController {
    private final BlogService service;

    public BlogController(BlogService service) {
        this.service = service;
    }

    @GetMapping("/public/blog/posts")
    public PageResponse<PostResponse> posts(Pageable pageable) {
        return service.publicPosts(pageable);
    }

    @GetMapping("/public/blog/posts/{slug}")
    public PostResponse post(@PathVariable String slug) {
        return service.publicPost(slug);
    }

    @PostMapping("/admin/blog/posts")
    @PreAuthorize("hasRole('ADMIN')")
    public PostResponse create(Authentication authentication, @Valid @RequestBody PostRequest request) {
        return service.create((UUID) authentication.getPrincipal(), request);
    }

    @PutMapping("/admin/blog/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PostResponse update(Authentication authentication, @PathVariable UUID id,
                               @Valid @RequestBody PostRequest request) {
        return service.update((UUID) authentication.getPrincipal(), id, request);
    }

    @PatchMapping("/admin/blog/posts/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public PostResponse publish(Authentication authentication, @PathVariable UUID id) {
        return service.publish((UUID) authentication.getPrincipal(), id);
    }

    @DeleteMapping("/admin/blog/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Authentication authentication, @PathVariable UUID id) {
        service.archive((UUID) authentication.getPrincipal(), id);
    }

    @PostMapping("/blog/posts/{id}/comments")
    @PreAuthorize("hasRole('STUDENT')")
    public CommentResponse comment(Authentication authentication, @PathVariable UUID id,
                                   @Valid @RequestBody CommentRequest request) {
        return service.comment((UUID) authentication.getPrincipal(), id, request.content());
    }

    @GetMapping("/blog/posts/{id}/comments")
    public PageResponse<CommentResponse> comments(@PathVariable UUID id, Pageable pageable) {
        return service.visibleComments(id, pageable);
    }

    @PatchMapping("/admin/blog/comments/{id}/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public void hide(Authentication authentication, @PathVariable UUID id) {
        service.hideComment((UUID) authentication.getPrincipal(), id);
    }
}
