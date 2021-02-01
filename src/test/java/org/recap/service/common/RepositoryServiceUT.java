package org.recap.service.common;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.repository.jpa.*;

import static org.junit.Assert.assertNotNull;

public class RepositoryServiceUT extends BaseTestCaseUT {

    @InjectMocks
    RepositoryService repositoryService;

    @Mock
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @Mock
    private ReportDetailRepository reportDetailRepository;

    @Mock
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Test
    public void getBibliographicDetailsRepository(){
        BibliographicDetailsRepository bibliographicDetailsRepository = repositoryService.getBibliographicDetailsRepository();
        assertNotNull(bibliographicDetailsRepository);
    }
    @Test
    public void getItemDetailsRepository(){
        ItemDetailsRepository itemDetailsRepository = repositoryService.getItemDetailsRepository();
        assertNotNull(itemDetailsRepository);
    }
    @Test
    public void getReportDetailRepository(){
        ReportDetailRepository reportDetailRepository = repositoryService.getReportDetailRepository();
        assertNotNull(reportDetailRepository);
    }
    @Test
    public void getItemStatusDetailsRepository(){
        ItemStatusDetailsRepository itemStatusDetailsRepository = repositoryService.getItemStatusDetailsRepository();
        assertNotNull(itemStatusDetailsRepository);
    }
    @Test
    public void getInstitutionDetailsRepository(){
        InstitutionDetailsRepository institutionDetailsRepository = repositoryService.getInstitutionDetailsRepository();
        assertNotNull(institutionDetailsRepository);
    }
    @Test
    public void getItemChangeLogDetailsRepository(){
        ItemChangeLogDetailsRepository itemChangeLogDetailsRepository = repositoryService.getItemChangeLogDetailsRepository();
        assertNotNull(itemChangeLogDetailsRepository);
    }
    @Test
    public void getCollectionGroupDetailsRepository(){
        CollectionGroupDetailsRepository collectionGroupDetailsRepository = repositoryService.getCollectionGroupDetailsRepository();
        assertNotNull(collectionGroupDetailsRepository);
    }
}
