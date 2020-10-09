package org.recap.model.jpa;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "generic_patron_t", schema = "recap", catalog = "")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class GenericPatronEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "GENERIC_PATRON_ID")
    private Integer genericPatronId;

    @Column(name = "REQUESTING_INST_ID")
    private Integer requestingInstitutionId;

    @Column(name = "ITEM_OWN_INST_ID")
    private Integer itemOwningInstitutionId;

    @Column(name = "EDD_GENERIC_PATRON")
    private String eddGenericPatron;

    @Column(name = "RETRIEVAL_GENERIC_PARTRON")
    private String retrievalGenericPatron;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "REQUESTING_INST_ID", insertable = false, updatable = false)
    private InstitutionEntity requestingInstitutionEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ITEM_OWN_INST_ID", insertable = false, updatable = false)
    private InstitutionEntity owningInstitutionEntity;
}
