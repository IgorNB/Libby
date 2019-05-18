package com.lig.libby.controller.adapter.userui.dto;

import com.lig.libby.domain.core.AbstractPersistentObject;
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
public class UserPublicDto extends AbstractPersistentObject {
    private String name;
    private String imageUrl;
}