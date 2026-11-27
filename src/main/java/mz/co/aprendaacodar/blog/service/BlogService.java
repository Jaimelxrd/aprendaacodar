package mz.co.aprendaacodar.blog.service;

import mz.co.aprendaacodar.audit.service.AuditService;
import mz.co.aprendaacodar.blog.dto.BlogDtos.CommentResponse;
import mz.co.aprendaacodar.blog.dto.BlogDtos.PostRequest;
import mz.co.aprendaacodar.blog.dto.BlogDtos.PostResponse;
import mz.co.aprendaacodar.blog.entity.BlogComment;
import mz.co.aprendaacodar.blog.entity.BlogPost;
import mz.co.aprendaacodar.blog.repository.BlogCommentRepository;
import mz.co.aprendaacodar.blog.repository.BlogPostRepository;
import mz.co.aprendaacodar.common.enums.Enums.BlogPostStatus;
import mz.co.aprendaacodar.common.enums.Enums.CommentStatus;
import mz.co.aprendaacodar.common.enums.Enums.UserRole;
import mz.co.aprendaacodar.common.exception.BusinessException;
import mz.co.aprendaacodar.common.exception.ForbiddenOperationException;
import mz.co.aprendaacodar.common.exception.ResourceNotFoundException;
import mz.co.aprendaacodar.common.response.PageResponse;
import mz.co.aprendaacodar.storage.StorageService;
import mz.co.aprendaacodar.user.entity.User;
import mz.co.aprendaacodar.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class BlogService {
    private final BlogPostRepository posts;
    private final BlogCommentRepository comments;
    private final UserRepository users;
    private final StorageService storage;
    private final AuditService audit;

    public BlogService(BlogPostRepository posts, BlogCommentRepository comments, UserRepository users,
                       StorageService storage, AuditService audit) {
        this.posts = posts;
        this.comments = comments;
        this.users = users;
        this.storage = storage;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> publicPosts(Pageable pageable) {
        return PageResponse.from(posts.findAllByStatusOrderByPublishedAtDesc(BlogPostStatus.PUBLISHED, pageable), this::post);
    }

    @Transactional(readOnly = true)
    public PostResponse publicPost(String slug) {
        return post(posts.findBySlugAndStatus(slug, BlogPostStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Artigo não encontrado.")));
    }

    @Transactional
    public PostResponse create(UUID adminId, PostRequest request) {
        User admin = user(adminId);
        BlogPost post = posts.save(new BlogPost(request.title(), request.excerpt(), request.content(),
                request.coverImageKey(), admin));
        audit.record(admin, "BLOG_POST_CREATED", "BlogPost", post.getId(), Map.of("slug", post.getSlug()));
        return post(post);
    }

    @Transactional
    public PostResponse update(UUID adminId, UUID id, PostRequest request) {
        BlogPost post = findPost(id);
        post.update(request.title(), request.excerpt(), request.content(), request.coverImageKey());
        audit.record(user(adminId), "BLOG_POST_UPDATED", "BlogPost", id, Map.of("slug", post.getSlug()));
        return post(post);
    }

    @Transactional
    public PostResponse publish(UUID adminId, UUID id) {
        BlogPost post = findPost(id);
        post.publish();
        audit.record(user(adminId), "BLOG_POST_PUBLISHED", "BlogPost", id, Map.of());
        return post(post);
    }

    @Transactional
    public void archive(UUID adminId, UUID id) {
        findPost(id).archive();
        audit.record(user(adminId), "BLOG_POST_ARCHIVED", "BlogPost", id, Map.of());
    }

    @Transactional
    public CommentResponse comment(UUID studentId, UUID postId, String content) {
        BlogPost post = findPost(postId);
        if (post.getStatus() != BlogPostStatus.PUBLISHED) {
            throw new BusinessException("Não é possível comentar num artigo não publicado.");
        }
        User student = user(studentId);
        if (student.getRole() != UserRole.STUDENT) {
            throw new ForbiddenOperationException("Apenas estudantes podem comentar.");
        }
        return comment(comments.save(new BlogComment(post, student, content.trim())));
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> visibleComments(UUID postId, Pageable pageable) {
        BlogPost post = findPost(postId);
        if (post.getStatus() != BlogPostStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Artigo não encontrado.");
        }
        return PageResponse.from(comments.findAllByPostIdAndStatusOrderByCreatedAt(
                postId, CommentStatus.VISIBLE, pageable), this::comment);
    }

    @Transactional
    public void hideComment(UUID adminId, UUID id) {
        BlogComment comment = comments.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado."));
        comment.hide(user(adminId));
        audit.record(user(adminId), "BLOG_COMMENT_HIDDEN", "BlogComment", id, Map.of());
    }

    private BlogPost findPost(UUID id) {
        return posts.findById(id).orElseThrow(() -> new ResourceNotFoundException("Artigo não encontrado."));
    }

    private User user(UUID id) {
        return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado."));
    }

    private PostResponse post(BlogPost post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getSlug(), post.getExcerpt(), post.getContent(),
                post.getCoverImageKey() == null ? null : storage.url(post.getCoverImageKey(), true),
                post.getAuthor().getId(), post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName(),
                post.getPublishedAt());
    }

    private CommentResponse comment(BlogComment comment) {
        return new CommentResponse(comment.getId(), comment.getAuthor().getId(),
                comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName(),
                comment.getContent(), comment.getCreatedAt());
    }
}
