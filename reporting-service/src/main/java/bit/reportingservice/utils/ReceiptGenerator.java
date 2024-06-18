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
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReceiptGenerator {
    private final CampaignRepository campaignRepository;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private static final float BORDER_WIDTH = 1;
    private static final float BORDER_RADIUS = 25;
    private static final float OPACITY = 0.44f;
    private static final float RED = 151 / 255f;
    private static final float GREEN = 151 / 255f;
    private static final float BLUE = 151 / 255f;
    private static final int PRODUCT_INFO_HEIGHT = 30; // Height for each product info line
    private static final int TITLE_HEIGHT = 20; // Height for title
    private static final int BOTTOM_MARGIN = 5; // Fixed margin at the bottom for the "TOTAL" text
    private static final int FIXED_HEADER_HEIGHT = 70; // Height for fixed elements like headers, etc.

    /**
     * Generates a PDF receipt for a given {@link SaleReport}.
     *
     * @param saleReport The {@link SaleReport} for which the receipt is to be generated.
     * @return A byte array containing the generated PDF receipt.
     * @throws IOException If an error occurs while creating the PDF document.
     */
    public byte[] generate(SaleReport saleReport) throws IOException {
        try(PDDocument document = new PDDocument()){
            PDPage page = new PDPage(PDRectangle.A4);

            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true)){
                InputStream fontStreamRegular = getClass().getResourceAsStream("/fonts/Inter-Regular.ttf");
                InputStream fontStreamSemiBold = getClass().getResourceAsStream("/fonts/Inter-SemiBold.ttf");
                PDType0Font fontRegular = PDType0Font.load(document, fontStreamRegular);
                PDType0Font fontSemiBold = PDType0Font.load(document, fontStreamSemiBold);

                // Load the image
                InputStream imageStream = getClass().getResourceAsStream("/images/32bitlogo.png");
                if (imageStream == null) {
                    throw new IOException("Image file not found");
                }
                byte[] imageBytes = imageStream.readAllBytes();
                PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageBytes, "32bitlogo.png");

                drawHeader(contentStream, fontRegular, pdImage, saleReport.getId());
                drawBody(contentStream, fontSemiBold, fontRegular, saleReport);
                drawFooter(contentStream, fontSemiBold, fontRegular, saleReport);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private void drawHeader(PDPageContentStream contentStream, PDType0Font font, PDImageXObject image, Long id) throws IOException {
        // Set header dimensions and position
        int headerWidth = 379;
        int headerHeight = 126;
        float headerX = (PDRectangle.A4.getWidth() - headerWidth) / 2;
        int headerY = 610;

        // Create an extended graphics state for opacity
        drawRectangle(contentStream, headerX, headerY, headerWidth, headerHeight);
        contentStream.closeAndStroke();

        // Draw the image
        int imageWidth = 209;
        int imageHeight = 60;
        float imageX = (PDRectangle.A4.getWidth() - imageWidth) / 2;
        int imageY = 660;
        contentStream.drawImage(image, imageX, imageY, imageWidth, imageHeight);

        // Draw the text below the image
        float textX = (PDRectangle.A4.getWidth() - font.getStringWidth("Price No : 3") / 1000f * 17) / 2; // Center the text
        int textY = imageY - 25; // 17px below the image
        contentStream.setFont(font, 12);
        contentStream.setNonStrokingColor(53 / 255f, 53 / 255f, 53 / 255f);
        contentStream.beginText();
        contentStream.newLineAtOffset(textX, textY);
        contentStream.showText("Sell No : " + id);
        contentStream.endText();
    }

    private void drawBody(PDPageContentStream contentStream, PDType0Font fontSemiBold, PDType0Font fontRegular, SaleReport saleReport) throws IOException {
        int totalProductInfoHeight = saleReport.getProducts().size() * PRODUCT_INFO_HEIGHT;
        int bodyHeight = FIXED_HEADER_HEIGHT + TITLE_HEIGHT + totalProductInfoHeight + BOTTOM_MARGIN;

        // Set body dimensions and position
        int bodyWidth = 379;
        float bodyX = (PDRectangle.A4.getWidth() - bodyWidth) / 2;

        // Calculate the height of the header part
        int headerHeight = 126;
        int headerDistance = 34; // Distance between header and body
        float bodyY = PDRectangle.A4.getHeight() - headerHeight - headerDistance - bodyHeight - 80;

        float titleY = getTitleY(contentStream, fontSemiBold, bodyX, bodyY, bodyWidth, bodyHeight);
        contentStream.showText("Products in Bag");
        contentStream.newLineAtOffset(180, 0);
        contentStream.showText("Amount");
        contentStream.newLineAtOffset(140, 0);
        contentStream.showText("Price");
        contentStream.endText();

        // Calculate the width of the "TL" text
        float tlWidth = fontRegular.getStringWidth("TL") / 1000 * 12;

        // Draw products
        float productY = titleY - 30; // Initial Y position for products
        contentStream.setFont(fontRegular, 12);
        float priceX;

        for (Product product : saleReport.getProducts()) {
            contentStream.beginText();
            contentStream.newLineAtOffset(bodyX + 15, productY);
            contentStream.showText(product.getName().toUpperCase());
            contentStream.newLineAtOffset(180, 0);
            contentStream.showText(product.getQuantity() + "X");
            // Calculate the x-coordinate for the price
            priceX = bodyX + 350 - tlWidth - fontRegular.getStringWidth(decimalFormat.format(product.getPrice())) / 1000 * 12;
            contentStream.newLineAtOffset(priceX - (bodyX + 180), 0);
            double priceWithCampaign = calcDiscount(product);
            contentStream.showText(decimalFormat.format(priceWithCampaign) + "TL");
            contentStream.endText();
            productY -= 30;
        }

        float lineY = productY + 15;
        contentStream.setStrokingColor(RED, GREEN, BLUE);
        contentStream.setLineWidth(1); // Set line width as needed
        PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
        graphicsState.setStrokingAlphaConstant(0.3f);
        contentStream.setGraphicsStateParameters(graphicsState);
        contentStream.moveTo(bodyX, lineY);
        contentStream.lineTo(bodyX + bodyWidth, lineY);
        contentStream.stroke();

        // Draw "TOTAL" text
        float totalTextY = lineY - 28; // Distance to the line above
        contentStream.setNonStrokingColor(0, 0, 0);
        contentStream.setFont(fontRegular, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(bodyX + 15, totalTextY);
        contentStream.showText("TOTAL");
        contentStream.endText();

        // Draw total price text
        priceX = bodyX + 365 - tlWidth - fontRegular.getStringWidth(decimalFormat.format(saleReport.getTotalPrice())) / 1000 * 12;
        contentStream.setNonStrokingColor(0, 0, 0);
        contentStream.setFont(fontRegular, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(priceX, totalTextY);
        contentStream.showText(decimalFormat.format(saleReport.getTotalPrice()) + "TL");
        contentStream.endText();
    }

    private void drawFooter(PDPageContentStream contentStream, PDType0Font fontSemiBold, PDType0Font fontRegular, SaleReport saleReport) throws IOException {
        int totalProductInfoHeight = saleReport.getProducts().size() * PRODUCT_INFO_HEIGHT;
        int bodyHeight = FIXED_HEADER_HEIGHT + TITLE_HEIGHT + totalProductInfoHeight + BOTTOM_MARGIN;

        int headerHeight = 126;
        int headerDistance = 34;

        int footerWidth = 379;
        int footerHeight = 81;
        float footerX = (PDRectangle.A4.getWidth() - footerWidth) / 2;
        float footerY = PDRectangle.A4.getHeight() - headerHeight - headerDistance - bodyHeight - 170;

        float titleY = getTitleY(contentStream, fontSemiBold, footerX, footerY, footerWidth, footerHeight);
        contentStream.showText("Payment Method");
        contentStream.newLineAtOffset(180, 0);
        contentStream.showText("Change");
        contentStream.newLineAtOffset(140, 0);
        contentStream.showText("Date");
        contentStream.endText();

        float paymentY = titleY - 30;
        contentStream.setFont(fontRegular, 12);

        // Format the date
        LocalDateTime completedDate = saleReport.getCompletedDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        String formattedDate = completedDate.format(formatter);

        // Calculate the width of the "Date" title and the formatted date
        float dateTitleWidth = fontSemiBold.getStringWidth("Date") / 1000 * 12;
        float formattedDateWidth = fontRegular.getStringWidth(formattedDate) / 1000 * 12;

        // Calculate the offset to align the end of the date with the end of the "Date" title
        float dateOffset = dateTitleWidth - formattedDateWidth;

        // Calculate the width of the "Change" title and the change value
        String changeText = decimalFormat.format(saleReport.getChange()) + "TL";
        float changeTitleWidth = fontSemiBold.getStringWidth("Change") / 1000 * 12;
        float changeValueWidth = fontRegular.getStringWidth(changeText) / 1000 * 12;

        // Calculate the offset to align the end of the change value with the end of the "Change" title
        float changeOffset = changeTitleWidth - changeValueWidth;

        contentStream.beginText();
        contentStream.newLineAtOffset(footerX + 15, paymentY);
        contentStream.showText(saleReport.getPaymentMethod().toString().toUpperCase());
        contentStream.newLineAtOffset(180 + changeOffset, 0);
        contentStream.showText(changeText);
        contentStream.newLineAtOffset(140 - changeOffset + dateOffset, 0);
        contentStream.showText(formattedDate);
        contentStream.endText();
    }

    private double calcDiscount(Product product) {
        int quantity = product.getQuantity() + product.getReturnedQuantity();
        double discount;
        double priceWithCampaign = quantity * product.getPrice();
        int timesApplied;
        Optional<Campaign> campaign = campaignRepository.findByName(product.getAppliedCampaign());
        if (campaign.isPresent()){
            timesApplied = quantity / campaign.get().getNeededQuantity();

            if (campaign.get().getDiscountType().equals(DiscountType.FIXED_AMOUNT)){
                discount = campaign.get().getDiscountAmount() * timesApplied;
            }
            else{
                discount = (product.getPrice() * (campaign.get().getDiscountAmount() / 100)) * (campaign.get().getNeededQuantity() * timesApplied);
            }

            priceWithCampaign -= discount;
        }
        return priceWithCampaign;
    }

    private static void drawRectangle(PDPageContentStream contentStream, float headerX, float headerY, float headerWidth, float headerHeight) throws IOException {
        PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
        graphicsState.setStrokingAlphaConstant(OPACITY);
        contentStream.setGraphicsStateParameters(graphicsState);

        contentStream.setStrokingColor(RED, GREEN, BLUE);
        contentStream.setLineWidth(BORDER_WIDTH);
        contentStream.moveTo(headerX + BORDER_RADIUS, headerY);
        contentStream.lineTo(headerX + headerWidth - BORDER_RADIUS, headerY);
        contentStream.curveTo(headerX + headerWidth, headerY, headerX + headerWidth, headerY, headerX + headerWidth, headerY + BORDER_RADIUS);
        contentStream.lineTo(headerX + headerWidth, headerY + headerHeight - BORDER_RADIUS);
        contentStream.curveTo(headerX + headerWidth, headerY + headerHeight, headerX + headerWidth, headerY + headerHeight, headerX + headerWidth - BORDER_RADIUS, headerY + headerHeight);
        contentStream.lineTo(headerX + BORDER_RADIUS, headerY + headerHeight);
        contentStream.curveTo(headerX, headerY + headerHeight, headerX, headerY + headerHeight, headerX, headerY + headerHeight - BORDER_RADIUS);
        contentStream.lineTo(headerX, headerY + BORDER_RADIUS);
        contentStream.curveTo(headerX, headerY, headerX, headerY, headerX + BORDER_RADIUS, headerY);
    }

    private static float getTitleY(PDPageContentStream contentStream, PDType0Font fontSemiBold, float bodyX, float bodyY, int bodyWidth, int bodyHeight) throws IOException {
        drawRectangle(contentStream, bodyX, bodyY, bodyWidth, bodyHeight);
        contentStream.closePath();
        contentStream.stroke();

        // Draw titles
        float titleY = bodyY + bodyHeight - ReceiptGenerator.TITLE_HEIGHT - 10; // Initial Y position for titles
        contentStream.setFont(fontSemiBold, 12);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color for titles
        contentStream.beginText();
        contentStream.newLineAtOffset(bodyX + 15, titleY);
        return titleY;
    }
}