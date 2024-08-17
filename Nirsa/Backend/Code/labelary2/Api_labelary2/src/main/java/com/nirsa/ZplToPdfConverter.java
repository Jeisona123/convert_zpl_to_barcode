package com.nirsa;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ZplToPdfConverter {

    public static void main(String[] args) {
        String zplFilePath = "C:\\Proyectos_software\\Nirsa\\Backend\\Doc\\zebracode.zpl";
        String pdfFilePath = "C:\\Proyectos_software\\Nirsa\\Backend\\Pdf\\out.pdf";
        String imagePath = "C:\\Proyectos_software\\Nirsa\\Backend\\Img\\output.png";

        try {
            // Convertir ZPL a imagen
            convertZplToImage(zplFilePath, imagePath);

            // Crear PDF usando la imagen
            createPdfFromImage(imagePath, pdfFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void convertZplToImage(String zplFilePath, String imagePath) throws IOException {
        String zplContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(zplFilePath)));
        URL url = new URL("http://api.labelary.com/v1/printers/8dpmm/labels/4x6/0/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "image/png");
        connection.getOutputStream().write(zplContent.getBytes("UTF-8"));

        InputStream inputStream = connection.getInputStream();
        FileOutputStream outputStream = new FileOutputStream(imagePath);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();
    }

    private static void createPdfFromImage(String imagePath, String pdfFilePath) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(imagePath));

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
            document.addPage(page);

            PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, document);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pdImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
            }

            document.save(pdfFilePath);
            System.out.println("PDF generado correctamente en: " + pdfFilePath);
        }
    }
}
