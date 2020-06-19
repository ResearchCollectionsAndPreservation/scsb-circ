package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;
import org.recap.model.jpa.AbstractEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by akulak on 26/2/18 .
 */
@Entity
@Table(name = "deaccession_item_change_log_t", schema = "recap", catalog = "")
@AttributeOverride(name = "id", column = @Column(name = "CHANGE_LOG_ID"))
@Getter
@Setter
public class DeaccessionItemChangeLog extends AbstractEntity<Integer>  {
    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "OPERATION_TYPE")
    private String operationType;

    @Column(name = "RECORD_ID")
    private Integer recordId;

    @Column(name = "NOTES")
    private String notes;
}
