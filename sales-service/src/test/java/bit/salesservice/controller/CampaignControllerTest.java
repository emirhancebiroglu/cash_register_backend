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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Test
    void getCampaigns_Success() {
        List<ListCampaignsReq> mockCampaigns = new ArrayList<>();
        ListCampaignsReq campaign1 = new ListCampaignsReq();
        campaign1.setName("Campaign 1");
        mockCampaigns.add(campaign1);

        ListCampaignsReq campaign2 = new ListCampaignsReq();
        campaign2.setName("Campaign 2");
        mockCampaigns.add(campaign2);

        when(campaignService.getCampaigns(0, 10, null, null, null, "name", "ASC")).thenReturn(mockCampaigns);

        ResponseEntity<List<ListCampaignsReq>> response = campaignController.getCampaigns(0, 10, null, null, null, "name", "ASC");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        assertEquals("Campaign 1", response.getBody().get(0).getName());
        assertEquals("Campaign 2", response.getBody().get(1).getName());
    }
}
