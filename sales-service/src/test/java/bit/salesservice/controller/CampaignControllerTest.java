package bit.salesservice.controller;

import bit.salesservice.dto.AddAndUpdateCampaignReq;
import bit.salesservice.dto.ListCampaignsReq;
import bit.salesservice.service.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class CampaignControllerTest {
    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private CampaignController campaignController;

    private AddAndUpdateCampaignReq request;
    private Long campaignId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new AddAndUpdateCampaignReq();
        request.setName("Test Campaign");

        campaignId = 1L;

    }

    @Test
    void addCampaign_Success() {
        doNothing().when(campaignService).addCampaign(request);

        ResponseEntity<String> response = campaignController.addCampaign(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Campaign added successfully", response.getBody());
    }

    @Test
    void updateCampaign_Success() {
        doNothing().when(campaignService).updateCampaign(request, campaignId);

        ResponseEntity<String> response = campaignController.updateCampaign(request, campaignId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Campaign updated successfully", response.getBody());
    }

    @Test
    void inactivateCampaign_Success() {
        doNothing().when(campaignService).updateCampaign(request, campaignId);

        ResponseEntity<String> response = campaignController.inactivateCampaign(campaignId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Campaign inactivated successfully", response.getBody());
    }

    @Test
    void reactivateCampaign_Success() {
        Integer durationDays = 7;

        doNothing().when(campaignService).reactivateCampaign(campaignId, durationDays);

        ResponseEntity<String> response = campaignController.reactivateCampaign(campaignId, durationDays);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Campaign reactivated successfully", response.getBody());
    }

//    @Test
//    void getAllCampaigns_Success() {
//        List<ListCampaignsReq> campaigns = Collections.singletonList(new ListCampaignsReq());
//        when(campaignService.getAllCampaigns()).thenReturn(campaigns);
//
//        ResponseEntity<List<ListCampaignsReq>> response = campaignController.getAllCampaigns();
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(campaigns, response.getBody());
//    }
}
