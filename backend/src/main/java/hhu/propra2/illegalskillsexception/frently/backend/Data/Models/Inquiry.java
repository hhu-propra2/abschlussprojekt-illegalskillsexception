package hhu.propra2.illegalskillsexception.frently.backend.Data.Models;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    private BorrowArticle borrowArticle;
    @OneToOne
    private ApplicationUser borrower;
    @OneToOne
    private ApplicationUser lender;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;
    @Enumerated(EnumType.ORDINAL)
    private Status status;
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updated;

    public String timeSpanToString() {
        return startDate.toString() + " to " + endDate.toString();
    }

    public enum Status {
        OPEN, DECLINED, ACCEPTED
    }
}
