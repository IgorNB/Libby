package com.lig.libby.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import net.jcip.annotations.NotThreadSafe;
import org.hibernate.annotations.Formula;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NotThreadSafe
@Entity
@Table(name = Task.TABLE,
        indexes = {@Index(columnList = "book_name"),
                @Index(columnList = "book_authors"),
                @Index(columnList = "book_title"),
                @Index(columnList = "book_original_publication_year"),
                @Index(columnList = "book_original_title"),
                @Index(columnList = "book_isbn"),
                @Index(columnList = "book_isbn13")
        }
)
@Getter
@Setter
@ToString(callSuper = true)
@FieldNameConstants
public class Task extends GenericAbstractPersistentAuditingObject<User> {
    public static final String TABLE = "TASK";
    @Formula("NULL")
    private String q;
    @Transient
    private String command;
    @Transient
    private List<String> availableCommands;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne()
    @JoinColumn(name = Columns.ASSIGNEE_USER_ID, nullable = false)
    private User assignee;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne()
    @JoinColumn(name = Columns.BOOK_ID)
    private Book book;

    @Formula(" ( " +
            " SELECT SUM(loy." + LoyaltyTransaction.Columns.POINTS + ")" +
            " FROM " + LoyaltyTransaction.TABLE + " loy " +
            " WHERE loy." + LoyaltyTransaction.Columns.TASK_ID + " = " + Task.ID_COLUMN +
            " and loy." + LoyaltyTransaction.Columns.LOY_MEMBER_ID + " = " + Task.CREATED_BY_COLUMN +
            " ) "
    )
    private BigInteger points;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @Enumerated(EnumType.STRING)
    @Column(name = Columns.WORKFLOW_STEP, length = 25, nullable = false)
    private WorkflowStepEnum workflowStep;
    @Column(name = Columns.BOOK_NAME, nullable = true)
    private String bookName;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne()
    @JoinColumn(name = Columns.BOOK_WORK_ID)
    private Work bookWork;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne()
    @JoinColumn(name = Columns.BOOK_LANG_ID)
    private Lang bookLang;
    @Column(name = Columns.BOOK_ISBN, nullable = true)
    private String bookIsbn;

    @Column(name = Columns.BOOK_ISBN_13, nullable = true)
    private BigInteger bookIsbn13;
    @Column(name = Columns.BOOK_AUTHORS, nullable = true, length = 1000)
    private String bookAuthors;
    @Column(name = Columns.BOOK_ORIGINAL_PUBLICATION_YEAR, nullable = true)
    private Integer bookOriginalPublicationYear;
    @Column(name = Columns.BOOK_ORIGINAL_TITLE, nullable = true)
    private String bookOriginalTitle;
    @Column(name = Columns.BOOK_TITLE, nullable = true)
    private String bookTitle;
    @Column(name = Columns.BOOK_IMAGE_URL, nullable = true)
    private String bookImageUrl;
    @Column(name = Columns.BOOK_SMALL_IMAGE_URL, nullable = true)
    private String bookSmallImageUrl;

    public enum WorkflowStepEnum {
        APPROVED {
            @Override
            public List<WorkflowStepEnum> nextStates(UserDetails userDetails) {
                return new ArrayList<>();
            }
        },
        ESCALATED {
            @Override
            public List<WorkflowStepEnum> nextStates(UserDetails userDetails) {
                List<WorkflowStepEnum> states = new ArrayList<>();
                states.add(SUBMITTED);
                return states;
            }
        },
        SUBMITTED {
            @Override
            public List<WorkflowStepEnum> nextStates(UserDetails userDetails) {
                List<WorkflowStepEnum> states = new ArrayList<>();
                if (userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(a -> a.equals(Authority.Roles.ADMIN))) {
                    states.add(ESCALATED);
                    states.add(APPROVED);
                }
                return states;
            }
        },
        INIT {
            @Override
            public List<WorkflowStepEnum> nextStates(UserDetails userDetails) {
                List<WorkflowStepEnum> states = new ArrayList<>();
                states.add(SUBMITTED);
                return states;
            }
        };

        public abstract List<WorkflowStepEnum> nextStates(UserDetails userDetails);

        public List<String> nextStatesString(UserDetails userDetails) {
            return nextStates(userDetails).stream().map(Enum::name).collect(Collectors.toList());
        }
    }

    public static final class Columns {
        public static final String ASSIGNEE_USER_ID = "ASSIGNEE_USER_ID";
        public static final String WORKFLOW_STEP = "WORKFLOW_STEP";
        public static final String BOOK_ISBN = "book_isbn";
        public static final String BOOK_ISBN_13 = "book_isbn13";
        public static final String BOOK_AUTHORS = "book_authors";
        public static final String BOOK_ORIGINAL_PUBLICATION_YEAR = "book_original_publication_year";
        public static final String BOOK_ORIGINAL_TITLE = "book_original_title";
        public static final String BOOK_TITLE = "book_title";
        public static final String BOOK_IMAGE_URL = "book_image_url";
        public static final String BOOK_SMALL_IMAGE_URL = "book_small_image_url";
        public static final String BOOK_NAME = "BOOK_NAME";
        public static final String BOOK_LANG_ID = "BOOK_LANG_ID";
        public static final String BOOK_WORK_ID = "BOOK_WORK_ID";
        public static final String BOOK_ID = "BOOK_ID";

        private Columns() {
            throw new IllegalStateException("Utility class");
        }
    }
}
