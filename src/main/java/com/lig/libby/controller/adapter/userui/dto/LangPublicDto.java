package com.lig.libby.controller.adapter.userui.dto;

import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
@Getter
@Setter
@ToString(callSuper = true)
public class LangPublicDto extends GenericAbstractPersistentAuditingObject<UserPublicDto> {

    private String q;
    private String code;
}