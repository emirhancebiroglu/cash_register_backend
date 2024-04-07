package bit.salesservice.controller;

import bit.salesservice.dto.CampaignDTO;
import bit.salesservice.dto.CampaignReq;
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
    public ResponseEntity<String> addCampaign(@RequestBody CampaignDTO campaignDTO) {
        campaignService.addCampaign(campaignDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Campaign added successfully");
    }

    @PutMapping("/campaign/update/{campaignId}")
    public ResponseEntity<String> updateCampaign(@RequestBody CampaignDTO campaignDTO, @PathVariable Long campaignId) {
        campaignService.updateCampaign(campaignDTO, campaignId);
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
    public ResponseEntity<List<CampaignReq>> getAllCampaigns() {
        return ResponseEntity.status(HttpStatus.OK).body(campaignService.getAllCampaigns());
    }
}
