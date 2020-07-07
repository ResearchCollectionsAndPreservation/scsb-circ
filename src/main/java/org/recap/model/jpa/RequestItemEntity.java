package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by pvsubrah on 6/11/16.
 */
@Entity
@Table(name = "request_item_t", schema = "recap", catalog = "")
@AttributeOverride(name = "id", column = @Column(name = "REQUEST_ID"))
@Getter
@Setter
public class RequestItemEntity extends RequestItemAbstractEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ITEM_ID", referencedColumnName = "ITEM_ID", insertable = false, updatable = false)
    private ItemEntity itemEntity;

    @Column(name = "EMAIL_ID")
    private String emailId;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinTable(name = "bulk_request_t",
            joinColumns = @JoinColumn(name = "REQUEST_ID"),
            inverseJoinColumns = @JoinColumn(name = "BULK_REQUEST_ID"))
    private BulkRequestItemEntity bulkRequestItemEntity;

}
