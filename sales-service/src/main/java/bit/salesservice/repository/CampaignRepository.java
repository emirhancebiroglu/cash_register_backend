package bit.salesservice.repository;

import bit.salesservice.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByCodesContaining(String productCode);
    Campaign findFirstByCodesContaining(String code);
    Campaign findByName(String name);
}
