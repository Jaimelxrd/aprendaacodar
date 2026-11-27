package mz.co.aprendaacodar.blog.repository;

import mz.co.aprendaacodar.blog.entity.BlogComment;
import mz.co.aprendaacodar.common.enums.Enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BlogCommentRepository extends JpaRepository<BlogComment, UUID> {
    Page<BlogComment> findAllByPostIdAndStatusOrderByCreatedAt(UUID id, CommentStatus status, Pageable pageable);
}
