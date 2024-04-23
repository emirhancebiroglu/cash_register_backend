package bit.salesservice.controller;

import bit.salesservice.dto.AddAndUpdateCampaignReq;
import bit.salesservice.dto.ListCampaignsReq;
import bit.salesservice.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing campaigns.
 */
@RestController
@RequestMapping("/api/cashier")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;

    /**
     * Endpoint for adding a new campaign.
     *
     * @param addAndUpdateCampaignReq the request body containing the details of the campaign to be added
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/campaign/add")
    public ResponseEntity<String> addCampaign(@RequestBody AddAndUpdateCampaignReq addAndUpdateCampaignReq) {
        campaignService.addCampaign(addAndUpdateCampaignReq);
        return ResponseEntity.status(HttpStatus.CREATED).body("Campaign added successfully");
    }

    /**
     * Endpoint for updating an existing campaign.
     *
     * @param addAndUpdateCampaignReq the request body containing the updated details of the campaign
     * @param campaignId              the ID of the campaign to be updated
     * @return ResponseEntity indicating the status of the operation
     */
    @PutMapping("/campaign/update/{campaignId}")
    public ResponseEntity<String> updateCampaign(@RequestBody AddAndUpdateCampaignReq addAndUpdateCampaignReq, @PathVariable Long campaignId) {
        campaignService.updateCampaign(addAndUpdateCampaignReq, campaignId);
        return ResponseEntity.status(HttpStatus.OK).body("Campaign updated successfully");
    }

    /**
     * Endpoint for inactivating a campaign.
     *
     * @param campaignId the ID of the campaign to be inactivated
     * @return ResponseEntity indicating the status of the operation
     */
    @DeleteMapping("/campaign/inactivate/{campaignId}")
    public ResponseEntity<String> inactivateCampaign(@PathVariable Long campaignId) {
        campaignService.inactivateCampaign(campaignId);
        return ResponseEntity.status(HttpStatus.OK).body("Campaign inactivated successfully");
    }

    /**
     * Endpoint for reactivating a campaign.
     *
     * @param campaignId    the ID of the campaign to be reactivated
     * @param durationDays  the duration (in days) for which the campaign will be active
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/campaign/reactivate/{campaignId}/{durationDays}")
    public ResponseEntity<String> reactivateCampaign(@PathVariable Long campaignId, @PathVariable Integer durationDays) {
        campaignService.reactivateCampaign(campaignId, durationDays);
        return ResponseEntity.status(HttpStatus.OK).body("Campaign reactivated successfully");
    }

    /**
     * Endpoint for retrieving campaigns.
     *
     * @return ResponseEntity containing the list of campaigns
     */
    @GetMapping("/campaign/list")
    public ResponseEntity<List<ListCampaignsReq>> getAllCampaigns() {
        return ResponseEntity.status(HttpStatus.OK).body(campaignService.getAllCampaigns());
    }
}
