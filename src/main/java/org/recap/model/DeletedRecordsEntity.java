package org.recap.model;

import lombok.Getter;
import lombok.Setter;
import org.recap.model.jpa.AbstractEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by rajeshbabuk on 8/5/17.
 */
@Entity
@Table(name = "DELETED_RECORDS_T", schema = "recap", catalog = "")
@AttributeOverride(name = "id", column = @Column(name = "DELETED_RECORDS_ID"))
@Getter
@Setter
public class DeletedRecordsEntity extends AbstractEntity<Integer>  {
    @Column(name = "RECORDS_TABLE")
    private String Records_Table;

    @Column(name = "RECORDS_PRIMARY_KEY")
    private String recordsPrimaryKey;

    @Column(name = "DELETED_REPORTED_STATUS")
    private String deletedReportedStatus;

    @Column(name = "DELETED_BY")
    private String deletedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DELETED_DATE")
    private Date deletedDate;

    @Column(name = "RECORDS_LOG")
    private String recordsLog;
}
