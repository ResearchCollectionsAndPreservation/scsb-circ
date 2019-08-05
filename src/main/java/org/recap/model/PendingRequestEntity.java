package org.recap.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "PENDING_REQUEST_T", schema = "recap", catalog = "")
public class PendingRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PENDING_ID")
    private Integer pendingId;

    @Column(name = "REQUEST_ID")
    private Integer requestId;

    @Column(name = "ITEM_ID")
    private Integer itemId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REQUEST_CREATED_DATE")
    private Date requestCreatedDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "REQUEST_ID", referencedColumnName = "REQUEST_ID", insertable = false, updatable = false)
    private RequestItemEntity requestItemEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ITEM_ID", referencedColumnName = "ITEM_ID", insertable = false, updatable = false)
    private ItemEntity itemEntity;

}
