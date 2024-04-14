package bit.reportingservice.utils;

import bit.reportingservice.entity.Product;
import bit.reportingservice.entity.SaleReport;
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

@Component
public class ReceiptGenerator {
    private static final String SEPARATOR = "----------------------------------------------------------------------";

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
                contentStream.showText("Sale   : " + "CREDIT_CARD");

                contentStream.newLineAtOffset(-150, -35);
                contentStream.showText(SEPARATOR);

                contentStream.newLineAtOffset(0, -20);
                for (Product product: saleReport.getProducts()){
                    contentStream.showText(product.getCode());
                    contentStream.newLineAtOffset(+100, 0);
                    contentStream.showText("( " + product.getQuantity() + "X " + product.getPrice() + " )");
                    if (product.getReturnedQuantity() > 0){
                        contentStream.newLineAtOffset(0, -15);
                        contentStream.showText(product.getReturnedQuantity() + " returned");
                        contentStream.newLineAtOffset(-100, 0);
                    }
                    else{
                        contentStream.newLineAtOffset(-100, -15);
                    }
                    contentStream.showText(product.getName());
                    contentStream.newLineAtOffset(+220, +7.5F);
                    String formattedPrice = decimalFormat.format(product.getQuantity() * product.getPrice());
                    contentStream.showText(formattedPrice);
                    contentStream.newLineAtOffset(-220, -22.5F);
                }

                contentStream.newLineAtOffset(0, -5);
                contentStream.showText(SEPARATOR);

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("MONEY TAKEN");

                contentStream.newLineAtOffset(+220, 0);
                contentStream.showText(decimalFormat.format(saleReport.getMoneyTaken()));

                if (saleReport.getReturnedMoney() > 0){
                    contentStream.newLineAtOffset(-220, -15);
                    contentStream.showText("RETURNED MONEY");

                    contentStream.newLineAtOffset(+216, 0);
                    contentStream.showText("-" + decimalFormat.format(saleReport.getReturnedMoney()));
                    contentStream.newLineAtOffset(+4, 0);
                }

                contentStream.newLineAtOffset(-220, -15);
                contentStream.showText("CHANGE");

                contentStream.newLineAtOffset(+216, 0);
                contentStream.showText("-" + decimalFormat.format(saleReport.getChange()));
                contentStream.newLineAtOffset(+4, 0);

                contentStream.newLineAtOffset(-220, -20);
                contentStream.showText(SEPARATOR);

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("TOTAL");

                contentStream.newLineAtOffset(+220, 0);
                contentStream.showText(decimalFormat.format(saleReport.getTotalPrice()));

                if (saleReport.isCancelled()){
                    contentStream.newLineAtOffset(-120, -45);
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
