package mz.co.aprendaacodar.blog.service;
import mz.co.aprendaacodar.blog.dto.BlogDtos.*;import mz.co.aprendaacodar.blog.entity.*;import mz.co.aprendaacodar.blog.repository.*;import mz.co.aprendaacodar.common.enums.Enums.*;import mz.co.aprendaacodar.common.exception.*;import mz.co.aprendaacodar.user.entity.User;import mz.co.aprendaacodar.user.repository.UserRepository;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import java.util.*;
import mz.co.aprendaacodar.storage.StorageService;
@Service public class BlogService {
 private final BlogPostRepository posts;private final BlogCommentRepository comments;private final UserRepository users;private final StorageService storage;
 public BlogService(BlogPostRepository p,BlogCommentRepository c,UserRepository u,StorageService storage){posts=p;comments=c;users=u;this.storage=storage;}
 @Transactional(readOnly=true) public List<PostResponse> publicPosts(){return posts.findAllByStatusOrderByPublishedAtDesc(BlogPostStatus.PUBLISHED).stream().map(this::post).toList();}
 @Transactional(readOnly=true) public PostResponse publicPost(String slug){return post(posts.findBySlugAndStatus(slug,BlogPostStatus.PUBLISHED).orElseThrow(()->new ResourceNotFoundException("Artigo não encontrado.")));}
 @Transactional public PostResponse create(UUID admin,PostRequest r){return post(posts.save(new BlogPost(r.title(),r.excerpt(),r.content(),r.coverImageKey(),user(admin))));}
 @Transactional public PostResponse update(UUID id,PostRequest r){BlogPost p=findPost(id);p.update(r.title(),r.excerpt(),r.content(),r.coverImageKey());return post(p);}
 @Transactional public PostResponse publish(UUID id){BlogPost p=findPost(id);p.publish();return post(p);}@Transactional public void archive(UUID id){findPost(id).archive();}
 @Transactional public CommentResponse comment(UUID student,UUID postId,String content){BlogPost p=findPost(postId);if(p.getStatus()!=BlogPostStatus.PUBLISHED)throw new BusinessException("Não é possível comentar num artigo não publicado.");User u=user(student);if(u.getRole()!=UserRole.STUDENT)throw new ForbiddenOperationException("Apenas estudantes podem comentar.");return comment(comments.save(new BlogComment(p,u,content.trim())));}
 @Transactional(readOnly=true) public List<CommentResponse> visibleComments(UUID postId){BlogPost p=findPost(postId);if(p.getStatus()!=BlogPostStatus.PUBLISHED)throw new ResourceNotFoundException("Artigo não encontrado.");return comments.findAllByPostIdAndStatusOrderByCreatedAt(postId,CommentStatus.VISIBLE).stream().map(this::comment).toList();}
 @Transactional public void hideComment(UUID admin,UUID id){BlogComment c=comments.findById(id).orElseThrow(()->new ResourceNotFoundException("Comentário não encontrado."));c.hide(user(admin));}
 private BlogPost findPost(UUID id){return posts.findById(id).orElseThrow(()->new ResourceNotFoundException("Artigo não encontrado."));}private User user(UUID id){return users.findById(id).orElseThrow(()->new ResourceNotFoundException("Utilizador não encontrado."));}
 private PostResponse post(BlogPost p){return new PostResponse(p.getId(),p.getTitle(),p.getSlug(),p.getExcerpt(),p.getContent(),p.getCoverImageKey()==null?null:storage.url(p.getCoverImageKey(),true),p.getAuthor().getId(),p.getAuthor().getFirstName()+" "+p.getAuthor().getLastName(),p.getPublishedAt());}
 private CommentResponse comment(BlogComment c){return new CommentResponse(c.getId(),c.getAuthor().getId(),c.getAuthor().getFirstName()+" "+c.getAuthor().getLastName(),c.getContent(),c.getCreatedAt());}
}
