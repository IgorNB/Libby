package com.lig.libby.controller.adapter.userui.dto;

import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import net.jcip.annotations.NotThreadSafe;

import java.math.BigInteger;

@NotThreadSafe
@Getter
@Setter
@ToString(callSuper = true)
@FieldNameConstants
public class BookPublicDto extends GenericAbstractPersistentAuditingObject<UserPublicDto> {
    private String q;

    private String name;

    private WorkPublicDto work;

    private LangPublicDto lang;

    private Float averageRating;

    private BigInteger ratingsCount;

    private String isbn;

    private BigInteger isbn13;

    private String authors;

    private Integer originalPublicationYear;

    private String originalTitle;

    private String title;

    private String imageUrl;

    private String smallImageUrl;
}
