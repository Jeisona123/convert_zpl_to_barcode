package com.nirsa;


import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.graphics.internal.ZebraImageI;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ZplToPdfConverter {

    public static void main(String[] args) {
        String zplFilePath = "C:\\Proyectos_software\\Nirsa\\Backend\\Doc\\zebracode.zpl";
        String pdfFilePath = "C:\\Proyectos_software\\Nirsa\\Backend\\pdf\\zebracode.pdf";

        try {
            String zplContent = new String(Files.readAllBytes(Paths.get(zplFilePath)));
            BufferedImage zplImage = convertZplToImage(zplContent);
            createPdfFromImage(zplImage, pdfFilePath);
            System.out.println("PDF generado exitosamente en: " + pdfFilePath);
        } catch (IOException | ConnectionException | ZebraPrinterLanguageUnknownException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage convertZplToImage(String zplContent) throws ConnectionException, ZebraPrinterLanguageUnknownException {
        // Conexión TCP ficticia solo para convertir el ZPL a imagen en memoria
        Connection connection = new TcpConnection("localhost", 6101);
        connection.open();
        ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

        if (printer.getPrinterControlLanguage() == PrinterLanguage.ZPL) {
            ZebraImageI image = printer.convertLabel(zplContent);
            BufferedImage bufferedImage = ImageIO.read(image.getImageInputStream());
            connection.close();
            return bufferedImage;
        } else {
            connection.close();
            throw new IllegalArgumentException("El archivo ZPL no es compatible con la impresora configurada.");
        }
    }

    private static void createPdfFromImage(BufferedImage image, String pdfFilePath) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDImageXObject pdImage = LosslessFactory.createFromImage(document, image);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.drawImage(pdImage, 20, 20);
        contentStream.close();

        document.save(pdfFilePath);
        document.close();
    }
}
