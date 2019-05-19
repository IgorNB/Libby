package com.lig.libby.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import net.jcip.annotations.NotThreadSafe;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.math.BigInteger;


@NotThreadSafe
@Entity
@Table(name = LoyaltyTransaction.TABLE,
        indexes = {@Index(columnList = LoyaltyTransaction.Columns.LOY_MEMBER_ID),
                @Index(columnList = LoyaltyTransaction.Columns.TASK_ID, unique = true),
        }
)
@Getter
@Setter
@ToString(callSuper = true)
@FieldNameConstants
public class LoyaltyTransaction extends GenericAbstractPersistentAuditingObject<User> implements LoyaltyAccrualRedemptionItem {
    public static final String TABLE = "LOY_TXN";
    @Formula("NULL")
    private String q;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne()
    @JoinColumn(name = Columns.LOY_MEMBER_ID, nullable = false)
    private User loyaltyMember;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne()
    @JoinColumn(name = Columns.TASK_ID)
    private Task task;

    @Column(name = Columns.POINTS)
    private BigInteger points;

    public static final class Columns {
        public static final String LOY_MEMBER_ID = "LOY_MEMBER_ID";
        public static final String TASK_ID = "TASK_ID";
        public static final String POINTS = "POINTS";

        private Columns() {
            throw new IllegalStateException("Utility class");
        }
    }
}
