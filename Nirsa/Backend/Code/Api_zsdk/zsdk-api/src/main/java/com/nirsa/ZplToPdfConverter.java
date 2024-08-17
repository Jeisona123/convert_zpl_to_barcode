package com.nirsa;

import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ZplToPdfConverter {

    public static void main(String[] args) {
        String zplFilePath = "C:\\Proyectos_software\\Nirsa\\Backend\\Doc\\zebracode.zpl";
        String outputPdfPath = "C:\\Proyectos_software\\Nirsa\\Backend\\pdf\\output.pdf";

        try {
            // Leer el contenido del archivo ZPL
            String zplContent = new String(Files.readAllBytes(Paths.get(zplFilePath)));

            // Conectar con la impresora Zebra (simulada para esta conversión)
            Connection connection = new TcpConnection("127.0.0.1", 6101);
            connection.open();
            ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

            // Enviar el ZPL a la impresora
            printer.sendCommand(zplContent);

            // Recuperar la etiqueta como imagen
            byte[] imageData = printer.retrieveFormatFromPrinter("LAST");
            BufferedImage image = ImageIO.read(new java.io.ByteArrayInputStream(imageData));

            // Guardar la imagen temporalmente
            File tempImageFile = new File("temp_image.png");
            ImageIO.write(image, "png", tempImageFile);

            // Crear un documento PDF
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            // Agregar la imagen al PDF
            PDImageXObject pdImage = PDImageXObject.createFromFile(tempImageFile.getAbsolutePath(), document);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.drawImage(pdImage, 100, 500);
            contentStream.close();

            // Guardar el documento PDF
            document.save(outputPdfPath);
            document.close();

            // Cerrar la conexión
            connection.close();

            // Eliminar la imagen temporal
            tempImageFile.delete();

            System.out.println("PDF generado correctamente en " + outputPdfPath);
        } catch (ZebraPrinterLanguageUnknownException | IOException | ConnectionException e) {
            e.printStackTrace();
        }
    }
}