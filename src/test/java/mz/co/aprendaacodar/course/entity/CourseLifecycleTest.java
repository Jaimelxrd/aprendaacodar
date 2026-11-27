package mz.co.aprendaacodar.course.entity;

import mz.co.aprendaacodar.common.enums.Enums.CourseStatus;
import mz.co.aprendaacodar.common.enums.Enums.CurriculumMode;
import mz.co.aprendaacodar.common.enums.Enums.UserRole;
import mz.co.aprendaacodar.user.entity.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseLifecycleTest {
    @Test
    void changesToPublishedCourseRequireReviewAgain() {
        User teacher = new User("Ada", "Lovelace", "ada@example.com", null, "hash", UserRole.TEACHER);
        User admin = new User("Admin", "User", "admin@example.com", null, "hash", UserRole.ADMIN);
        Course course = new Course("Java", "Short summary", "Description", null,
                new BigDecimal("100.00"), 10, null, null, CurriculumMode.MODULES, teacher);

        course.submit();
        course.approve(admin);
        assertEquals(CourseStatus.PUBLISHED, course.getStatus());

        course.update("Advanced Java", "New summary", "New description", null,
                new BigDecimal("120.00"), 12, null, null, CurriculumMode.MODULES);

        assertEquals(CourseStatus.PENDING_APPROVAL, course.getStatus());
        assertEquals("advanced-java", course.getSlug());
    }
}
