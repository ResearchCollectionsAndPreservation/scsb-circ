package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by rajeshbabuk on 10/10/17.
 */
@Entity
@Table(name = "bulk_request_item_t", schema = "recap", catalog = "")
@AttributeOverride(name = "id", column = @Column(name = "BULK_REQUEST_ID"))
@Getter
@Setter
public class BulkRequestItemEntity extends BulkRequestItemAbstractEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "REQUESTING_INST_ID", insertable = false, updatable = false)
    private InstitutionEntity institutionEntity;

    @OneToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "bulk_request_t",
            joinColumns = @JoinColumn(name = "BULK_REQUEST_ID"),
            inverseJoinColumns = @JoinColumn(name = "REQUEST_ID"))
    private List<RequestItemEntity> requestItemEntities;

}
