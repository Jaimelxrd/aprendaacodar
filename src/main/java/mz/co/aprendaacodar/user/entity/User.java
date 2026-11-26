package mz.co.aprendaacodar.user.entity;

import jakarta.persistence.*;
import mz.co.aprendaacodar.common.entity.BaseEntity;
import mz.co.aprendaacodar.common.enums.Enums.UserRole;
import mz.co.aprendaacodar.common.enums.Enums.UserStatus;
import java.time.Instant;

@Entity @Table(name = "users")
public class User extends BaseEntity {
    @Column(nullable=false, length=80) private String firstName;
    @Column(nullable=false, length=80) private String lastName;
    @Column(nullable=false, unique=true, length=254) private String email;
    @Column(length=30) private String phoneNumber;
    @Column(nullable=false) private String passwordHash;
    @Column(length=500) private String profilePhotoKey;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private UserRole role;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private UserStatus status = UserStatus.ACTIVE;
    @Column(nullable=false) private boolean emailVerified;
    private Instant lastLoginAt;
    protected User() {}
    public User(String firstName, String lastName, String email, String phoneNumber, String passwordHash, UserRole role) {
        this.firstName=firstName; this.lastName=lastName; this.email=email.toLowerCase(); this.phoneNumber=phoneNumber; this.passwordHash=passwordHash; this.role=role; this.status=UserStatus.ACTIVE;
    }
    public String getFirstName(){return firstName;} public String getLastName(){return lastName;} public String getEmail(){return email;}
    public String getPhoneNumber(){return phoneNumber;} public String getPasswordHash(){return passwordHash;} public UserRole getRole(){return role;}
    public UserStatus getStatus(){return status;} public Instant getLastLoginAt(){return lastLoginAt;} public String getProfilePhotoKey(){return profilePhotoKey;}
    public void updateProfile(String first, String last, String phone){firstName=first;lastName=last;phoneNumber=phone;}
 public void setStatus(UserStatus status){this.status=status;} public void setLastLoginAt(Instant value){lastLoginAt=value;} public void setEmail(String email){this.email=email.trim().toLowerCase();}
    public void setPasswordHash(String value){passwordHash=value;} public void setProfilePhotoKey(String value){profilePhotoKey=value;}
    public void setEmailVerified(boolean value){emailVerified=value;}
}
