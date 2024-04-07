package bit.salesservice.controller;

import bit.salesservice.dto.AddAndUpdateCampaignReq;
import bit.salesservice.dto.ListCampaignsReq;
import bit.salesservice.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cashier")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;

    @PostMapping("/campaign/add")
    public ResponseEntity<String> addCampaign(@RequestBody AddAndUpdateCampaignReq addAndUpdateCampaignReq) {
        campaignService.addCampaign(addAndUpdateCampaignReq);
        return ResponseEntity.status(HttpStatus.CREATED).body("Campaign added successfully");
    }

    @PutMapping("/campaign/update/{campaignId}")
    public ResponseEntity<String> updateCampaign(@RequestBody AddAndUpdateCampaignReq addAndUpdateCampaignReq, @PathVariable Long campaignId) {
        campaignService.updateCampaign(addAndUpdateCampaignReq, campaignId);
        return ResponseEntity.status(HttpStatus.OK).body("Campaign updated successfully");
    }

    @DeleteMapping("/campaign/inactivate/{campaignId}")
    public ResponseEntity<String> inactivateCampaign(@PathVariable Long campaignId) {
        campaignService.inactivateCampaign(campaignId);
        return ResponseEntity.status(HttpStatus.OK).body("Campaign inactivated successfully");
    }

    @PostMapping("/campaign/reactivate/{campaignId}/{durationDays}")
    public ResponseEntity<String> reactivateCampaign(@PathVariable Long campaignId, @PathVariable Integer durationDays) {
        campaignService.reactivateCampaign(campaignId, durationDays);
        return ResponseEntity.status(HttpStatus.OK).body("Campaign reactivated successfully");
    }


    @GetMapping("/campaign/list")
    public ResponseEntity<List<ListCampaignsReq>> getAllCampaigns() {
        return ResponseEntity.status(HttpStatus.OK).body(campaignService.getAllCampaigns());
    }
}
