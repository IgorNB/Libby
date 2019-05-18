package com.lig.libby.controller.adapter.userui.dto;

import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
@Getter
@Setter
@ToString(callSuper = true)
@FieldNameConstants
public class WorkPublicDto extends GenericAbstractPersistentAuditingObject<UserPublicDto> {
    private Integer booksCount;
}
