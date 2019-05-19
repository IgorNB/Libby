package com.lig.libby.controller.adapter.userui.dto;

import com.lig.libby.domain.Task;
import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import net.jcip.annotations.NotThreadSafe;

import java.math.BigInteger;
import java.util.List;

@NotThreadSafe
@Getter
@Setter
@ToString(callSuper = true)
@FieldNameConstants
public class TaskPublicDto extends GenericAbstractPersistentAuditingObject<UserPublicDto> {
    private String q;

    private String command;

    private List<String> availableCommands;

    private UserPublicDto assignee;

    private Task.WorkflowStepEnum workflowStep;

    private String bookName;

    private WorkPublicDto bookWork;

    private LangPublicDto bookLang;

    private String bookIsbn;

    private BigInteger bookIsbn13;

    private String bookAuthors;

    private Integer bookOriginalPublicationYear;

    private String bookOriginalTitle;

    private String bookTitle;

    private String bookImageUrl;

    private String bookSmallImageUrl;

    private BookPublicDto book;

    private BigInteger points;
}
