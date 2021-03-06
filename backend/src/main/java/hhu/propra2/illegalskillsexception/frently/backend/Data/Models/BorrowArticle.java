package hhu.propra2.illegalskillsexception.frently.backend.Data.Models;

import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class BorrowArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private ApplicationUser owner;
    private Double deposit;

    @Lob
    private String description;
    private Double dailyRate;
    private String location;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updated;
}
