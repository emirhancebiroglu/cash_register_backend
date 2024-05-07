package bit.reportingservice.utils;

import bit.reportingservice.entity.Campaign;
import bit.reportingservice.entity.DiscountType;
import bit.reportingservice.entity.Product;
import bit.reportingservice.entity.SaleReport;
import bit.reportingservice.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReceiptGenerator {
    private final CampaignRepository campaignRepository;
    private static final String SEPARATOR = "---------------------------------------------------------------------------";

    /**
     * Generates a PDF receipt for a given {@link SaleReport}.
     *
     * @param saleReport The {@link SaleReport} for which the receipt is to be generated.
     * @return A byte array containing the generated PDF receipt.
     * @throws IOException If an error occurs while creating the PDF document.
     */
    public byte[] generate(SaleReport saleReport) throws IOException {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        try(PDDocument document = new PDDocument()){
            PDPage page = new PDPage(PDRectangle.A6);

            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)){
                InputStream fontStream = getClass().getResourceAsStream("/fonts/Helvetica.ttf");
                PDType0Font font = PDType0Font.load(document, fontStream);

                contentStream.setFont(font, 11);

                float startY  = PDRectangle.A6.getHeight() - 30;

                contentStream.beginText();
                contentStream.newLineAtOffset(10, startY);
                contentStream.showText("Date        : " + formatCompletedDate(saleReport.getCompletedDate()));

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Sale No   : " + saleReport.getId());

                contentStream.newLineAtOffset(+150, +15);
                contentStream.showText("Sale   : " + saleReport.getPaymentMethod());

                contentStream.newLineAtOffset(-150, -35);
                contentStream.showText(SEPARATOR);

                contentStream.newLineAtOffset(0, -20);
                for (Product product: saleReport.getProducts()){
                    int quantity = product.getQuantity() + product.getReturnedQuantity();
                    double discount;
                    double priceWithCampaign = quantity * product.getPrice();
                    int timesApplied;
                    Optional<Campaign> campaign = campaignRepository.findByName(product.getAppliedCampaign());

                    if (campaign.isPresent()){
                        timesApplied = quantity / campaign.get().getNeededQuantity();

                        if (campaign.get().getDiscountType().equals(DiscountType.FIXED_AMOUNT)){
                            discount = (product.getPrice() - campaign.get().getDiscountAmount()) * timesApplied;
                        }
                        else{
                            discount = (product.getPrice() * (campaign.get().getDiscountAmount() / 100)) * (campaign.get().getNeededQuantity() * timesApplied);
                        }

                        priceWithCampaign -= discount;
                    }

                    contentStream.showText(product.getCode());
                    contentStream.newLineAtOffset(+85, 0);
                    contentStream.showText("( " + quantity + "X " + product.getPrice() + " )");

                    contentStream.newLineAtOffset(0, -15);
                    if (product.getReturnedQuantity() > 0){
                        contentStream.showText(product.getReturnedQuantity() + " returned");
                    }

                    if (product.getAppliedCampaign() != null){
                        contentStream.newLineAtOffset(+55, 0);
                        contentStream.setFont(font, 10);
                        contentStream.showText(product.getAppliedCampaign());
                        contentStream.setFont(font, 11);
                        contentStream.newLineAtOffset(-140, 0);
                    }
                    else{
                        contentStream.newLineAtOffset(-85, 0);
                    }

                    contentStream.showText(product.getName());
                    contentStream.newLineAtOffset(+230, +15F);
                    String formattedPrice = decimalFormat.format(priceWithCampaign);
                    contentStream.showText(formattedPrice);
                    contentStream.newLineAtOffset(-230, -30);
                }

                contentStream.newLineAtOffset(0, -5);
                contentStream.showText(SEPARATOR);

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("MONEY TAKEN");

                contentStream.newLineAtOffset(+230, 0);
                contentStream.showText(decimalFormat.format(saleReport.getMoneyTaken()));

                if (saleReport.getReturnedMoney() > 0){
                    contentStream.newLineAtOffset(-230, -15);
                    contentStream.showText("RETURNED MONEY");

                    contentStream.newLineAtOffset(+226, 0);
                    contentStream.showText("-" + decimalFormat.format(saleReport.getReturnedMoney()));
                    contentStream.newLineAtOffset(+4, 0);
                }

                contentStream.newLineAtOffset(-230, -15);
                contentStream.showText("CHANGE");

                contentStream.newLineAtOffset(+226, 0);
                contentStream.showText("-" + decimalFormat.format(saleReport.getChange()));
                contentStream.newLineAtOffset(+4, 0);

                contentStream.newLineAtOffset(-230, -20);
                contentStream.showText(SEPARATOR);

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("TOTAL");

                contentStream.newLineAtOffset(+230, 0);
                contentStream.showText(decimalFormat.format(saleReport.getTotalPrice()));

                if (saleReport.isCancelled()){
                    contentStream.newLineAtOffset(-130, -45);
                    contentStream.showText("CANCELLED");
                }


                contentStream.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private String formatCompletedDate(LocalDateTime completedDate) {
        LocalDate date = completedDate.toLocalDate();
        return date.format(DateTimeFormatter.ISO_DATE);
    }
}
