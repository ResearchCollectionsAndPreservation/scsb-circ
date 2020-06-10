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
@Table(name = "ACCESSION_T", schema = "recap", catalog = "")
@AttributeOverride(name = "id", column = @Column(name = "ACCESSION_ID"))
@Getter
@Setter
public class AccessionEntity extends AbstractEntity<Integer>  {
    @Column(name = "ACCESSION_REQUEST")
    private String accessionRequest;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "ACCESSION_STATUS")
    private String accessionStatus;
}
