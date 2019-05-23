package com.lig.libby.domain;

import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import net.jcip.annotations.NotThreadSafe;
import org.hibernate.annotations.Formula;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Document(collection = Task.TABLE)
@NotThreadSafe
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

    @DBRef
    private User assignee;

    @DBRef
    private Book book;

    @Field(Columns.WORKFLOW_STEP)
    @Enumerated(EnumType.STRING)
    private WorkflowStepEnum workflowStep;

    @Field(Columns.BOOK_NAME)
    private String bookName;

    @DBRef
    private Work bookWork;

    @DBRef
    private Lang bookLang;

    @Field(Columns.BOOK_ISBN)
    private String bookIsbn;

    @Field(Columns.BOOK_ISBN_13)
    private BigInteger bookIsbn13;

    @Field(Columns.BOOK_AUTHORS)
    private String bookAuthors;

    @Field(Columns.BOOK_ORIGINAL_PUBLICATION_YEAR)
    private Integer bookOriginalPublicationYear;

    @Field(Columns.BOOK_ORIGINAL_TITLE)
    private String bookOriginalTitle;

    @Field(Columns.BOOK_TITLE)
    private String bookTitle;

    @Field(Columns.BOOK_IMAGE_URL)
    private String bookImageUrl;

    @Field(Columns.BOOK_SMALL_IMAGE_URL)
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
        public static final String ASSIGNEE_USER_ID = "assigneeUserId";
        public static final String WORKFLOW_STEP = "workflowStep";
        public static final String BOOK_ISBN = "bookIsbn";
        public static final String BOOK_ISBN_13 = "bookIsbn13";
        public static final String BOOK_AUTHORS = "bookAuthors";
        public static final String BOOK_ORIGINAL_PUBLICATION_YEAR = "bookOriginalPublicationYear";
        public static final String BOOK_ORIGINAL_TITLE = "bookOriginalTitle";
        public static final String BOOK_TITLE = "bookTitle";
        public static final String BOOK_IMAGE_URL = "bookImageUrl";
        public static final String BOOK_SMALL_IMAGE_URL = "bookSmallImageUrl";
        public static final String BOOK_NAME = "bookName";
        public static final String BOOK_LANG_ID = "bookLangId";
        public static final String BOOK_WORK_ID = "bookWorkId";
        public static final String BOOK_ID = "bookId";

        private Columns() {
            throw new IllegalStateException("Utility class");
        }
    }
}
