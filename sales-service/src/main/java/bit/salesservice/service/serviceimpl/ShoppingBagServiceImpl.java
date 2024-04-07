package bit.salesservice.service.serviceimpl;

import bit.salesservice.dto.ProductInfo;
import bit.salesservice.dto.ProductReq;
import bit.salesservice.entity.Campaign;
import bit.salesservice.entity.Checkout;
import bit.salesservice.entity.DiscountType;
import bit.salesservice.entity.Product;
import bit.salesservice.exceptions.invalidquantity.InvalidQuantityException;
import bit.salesservice.exceptions.notinstocks.NotInStocksException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import bit.salesservice.exceptions.uncompletedcheckoutexception.UncompletedCheckoutException;
import bit.salesservice.repository.CampaignRepository;
import bit.salesservice.repository.CheckoutRepository;
import bit.salesservice.repository.ShoppingBagRepository;
import bit.salesservice.service.ShoppingBagService;
import bit.salesservice.utils.ProductInfoHttpRequest;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingBagServiceImpl implements ShoppingBagService {
    private final ShoppingBagRepository shoppingBagRepository;
    private final CheckoutRepository checkoutRepository;
    private final CampaignRepository campaignRepository;
    private final String jwtToken = HttpHeaders.AUTHORIZATION.substring(7);
    private static final Logger logger = LoggerFactory.getLogger(ShoppingBagServiceImpl.class);
    private final ProductInfoHttpRequest info;
    private static final String NOT_FOUND = "Product not found";


    @Override
    public void addProductToBag(ProductReq req) {
        logger.info("Adding product to shopping bag...");

        ProductInfo productInfo = info.getProductInfo(req.getCode(), jwtToken);

        checkIfProductAvailable(req, productInfo);

        if (req.getQuantity() <= 0){
            throw new InvalidQuantityException("Please provide a valid quantity.");
        }

        if (productInfo.getStockAmount() < req.getQuantity()){
            throw new NotInStocksException("There is not enough product in stocks.");
        }

        Checkout currentCheckout = getCurrentCheckout();
        Product existingProduct = shoppingBagRepository.findByCodeAndCheckout(req.getCode(), currentCheckout);

        if (existingProduct != null && existingProduct.isRemoved()) {
            existingProduct.setRemoved(false);
            existingProduct.setQuantity(req.getQuantity());
        } else if (existingProduct != null) {
            int newQuantity = existingProduct.getQuantity() + req.getQuantity();
            if (productInfo.getStockAmount() < newQuantity) {
                throw new NotInStocksException("There is not enough product in stocks.");
            }
            existingProduct.setQuantity(newQuantity);
        } else {
            existingProduct = new Product();
            existingProduct.setCode(req.getCode());
            existingProduct.setName(productInfo.getName());
            existingProduct.setQuantity(req.getQuantity());
            existingProduct.setPrice(productInfo.getPrice());
            existingProduct.setCheckout(currentCheckout);
            currentCheckout.setTotalPrice(0D);
        }

        updateCheckoutTotalForAddingProduct(existingProduct, productInfo.getPrice(), req);
        shoppingBagRepository.save(existingProduct);

    }


    @Override
    public void removeProductFromBag(Long id, Integer quantity) {
        logger.info("Removing product from shopping bag...");

        Checkout currentCheckout = getCurrentCheckout();

        Product product = shoppingBagRepository.findByIdAndCheckoutId(id, currentCheckout.getId())
                .orElseThrow(() -> new ProductNotFoundException(NOT_FOUND));

        int productQuantity = product.getQuantity();

        if (productQuantity < quantity){
            throw new InvalidQuantityException("Quantity is out of range");
        }

        if (productQuantity > quantity && quantity != 0){
            product.setQuantity(productQuantity - quantity);
        }
        else{
            product.setQuantity(0);
            product.setRemoved(true);
        }
        currentCheckout.setTotalPrice(currentCheckout.getTotalPrice() - product.getPrice() * quantity);

        shoppingBagRepository.save(product);
        checkoutRepository.save(currentCheckout);

        logger.info("Product removed successfully");
    }

    @Override
    public void removeAll() {
        logger.info("Removing all products from shopping bag...");

        Checkout currentCheckout = getCurrentCheckout();

        List<Product> products = shoppingBagRepository.findByCheckout(currentCheckout);

        for (Product product : products){
            product.setRemoved(true);
            product.setQuantity(0);
            shoppingBagRepository.save(product);
        }

        currentCheckout.setTotalPrice(0D);
        checkoutRepository.save(currentCheckout);

        logger.info("All products removed successfully");
    }

    @Override
    public List<ProductReq> getProductsInBagForCurrentCheckout() {
        logger.info("Getting products in shopping bag for current checkout...");

        Checkout currentCheckout = getCurrentCheckout();

        List<ProductReq> products;

        if (!currentCheckout.isCompleted()){
            products = shoppingBagRepository.findProductReqByCheckoutAndRemoved(currentCheckout);
            logger.info("Products fetched successfully.");
            return products;
        }

        logger.info("Checkout is completed. No products to retrieve.");
        return Collections.emptyList();
    }

    @Override
    public void returnProductFromBag(Long id, Integer quantityToReturn, Long checkoutId) {
        logger.info("Returning product from shopping bag...");

        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new ProductNotFoundException("Checkout not found"));

        if (checkout.isCompleted()) {
            Product product = shoppingBagRepository.findByIdAndCheckoutId(id, checkout.getId())
                    .orElseThrow(() -> new ProductNotFoundException(NOT_FOUND));

            int quantity = product.getQuantity();

            if (quantity < quantityToReturn) {
                throw new InvalidQuantityException("Quantity is out of range");
            }

            if (quantity > quantityToReturn) {
                product.setQuantity(quantity - quantityToReturn);
            } else {
                product.setQuantity(0);
                product.setReturned(true);
            }

            product.setReturnedQuantity(quantityToReturn);
            checkout.setTotalPrice(checkout.getTotalPrice() - product.getPrice() * product.getQuantity());
            checkout.setUpdatedDate(LocalDateTime.now());

            shoppingBagRepository.save(product);
            checkoutRepository.save(checkout);
        }
        else{
            throw new UncompletedCheckoutException("Checkout is not completed.");
        }

        logger.info("Product returned successfully");
    }

    private Checkout getCurrentCheckout() {
        Checkout currentCheckout = checkoutRepository.findFirstByOrderByIdDesc();
        if (currentCheckout == null) {
            currentCheckout = new Checkout();
            currentCheckout.setCreatedDate(LocalDateTime.now());
            currentCheckout.setUpdatedDate(LocalDateTime.now());

            checkoutRepository.save(currentCheckout);
        }
        return currentCheckout;
    }

    private void updateCheckoutTotalForAddingProduct(Product product, double total, ProductReq req){
        Checkout checkout = product.getCheckout();
        checkout.setTotalPrice(applyCampaignsAndUpdatePrice(product, total * req.getQuantity(), req));
        checkout.setUpdatedDate(LocalDateTime.now());
        checkoutRepository.save(checkout);
    }

    private static int getStockAmount(ProductReq req, ProductInfo productInfo) {
        boolean exists = productInfo.isExists();
        int stockAmount = productInfo.getStockAmount();

        if (!exists){
            throw new ProductNotFoundException("Product not exists or in stock");
        }

        if (stockAmount < req.getQuantity()) {
            throw new NotInStocksException("There is not enough product in stocks. Please provide a valid quantity");
        }
        return stockAmount;
    }

    private double applyCampaignsAndUpdatePrice(Product existingProduct, double total, ProductReq req) {
        Campaign campaign = campaignRepository.findFirstByCodesContaining(existingProduct.getCode());

        if (campaign == null || campaign.isInactive()) {
            return existingProduct.getCheckout().getTotalPrice() + total;
        }

        double discountAmount = calculateDiscountAmount(campaign, existingProduct, req);

        return existingProduct.getCheckout().getTotalPrice() + total - discountAmount;
    }

    private double calculateDiscountAmount(Campaign campaign, Product existingProduct, ProductReq req) {
        if (campaign.getDiscountType() == DiscountType.PERCENTAGE) {
            if (campaign.getNeededQuantity() > 1 && existingProduct.getQuantity() % campaign.getNeededQuantity() != 0) {
                return 0;
            }
            return (existingProduct.getPrice() * campaign.getDiscountAmount() / 100) * req.getQuantity();
        } else if (campaign.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            return campaign.getDiscountAmount() * req.getQuantity();
        }
        return 0;
    }

    private void checkIfProductAvailable(ProductReq req, ProductInfo productInfo){
        int stockAmount = getStockAmount(req, productInfo);
        boolean exists = productInfo.isExists();

        if (!exists || stockAmount == 0){
            throw new ProductNotFoundException("Product not exists or in stock");
        }
    }
}