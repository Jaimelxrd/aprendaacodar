package mz.co.aprendaacodar.user.entity;
import jakarta.persistence.*;import mz.co.aprendaacodar.common.entity.BaseEntity;
@Entity @Table(name="teacher_profiles") public class TeacherProfile extends BaseEntity {
 @OneToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(nullable=false,unique=true) private User user;
 @Column(columnDefinition="TEXT") private String biography;private String specialization;private String professionalTitle;
 protected TeacherProfile(){} public TeacherProfile(User user,String biography,String specialization,String professionalTitle){this.user=user;this.biography=biography;this.specialization=specialization;this.professionalTitle=professionalTitle;}
 public User getUser(){return user;}public String getBiography(){return biography;}public String getSpecialization(){return specialization;}public String getProfessionalTitle(){return professionalTitle;}public void update(String biography,String specialization,String title){this.biography=biography;this.specialization=specialization;this.professionalTitle=title;}
}
