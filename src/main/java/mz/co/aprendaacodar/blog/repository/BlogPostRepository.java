package mz.co.aprendaacodar.blog.repository;

import mz.co.aprendaacodar.blog.entity.BlogPost;
import mz.co.aprendaacodar.common.enums.Enums.BlogPostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BlogPostRepository extends JpaRepository<BlogPost, UUID> {
    Optional<BlogPost> findBySlugAndStatus(String slug, BlogPostStatus status);
    Page<BlogPost> findAllByStatusOrderByPublishedAtDesc(BlogPostStatus status, Pageable pageable);
}
