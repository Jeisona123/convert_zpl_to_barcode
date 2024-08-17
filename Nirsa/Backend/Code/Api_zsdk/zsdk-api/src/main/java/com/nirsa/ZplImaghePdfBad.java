package com.nirsa;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class ZplImaghePdfBad {

    public static void main(String[] args) {
        String zplFilePath = "C:\\Proyectos_software\\Nirsa\\Backend\\Doc\\zebracode.zpl";
        String outputPdfPath = "C:\\Proyectos_software\\Nirsa\\Backend\\Pdf\\output.pdf";

        try {
            // Leer el contenido del archivo ZPL
            String zplContent = new String(Files.readAllBytes(Paths.get(zplFilePath)));

            // Simular la generación de la imagen (en lugar de enviar a una impresora real)
            BufferedImage image = simulateZplToImage(zplContent);

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

            // Eliminar la imagen temporal
            tempImageFile.delete();

            System.out.println("PDF generado correctamente en " + outputPdfPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage simulateZplToImage(String zplContent) {
        // Aquí se simula la creación de una imagen a partir del contenido ZPL
        int width = 600;
        int height = 400;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Obtener el contexto gráfico de la imagen
        Graphics2D graphics = image.createGraphics();

        // Establecer el fondo blanco y las propiedades de la fuente
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial", Font.PLAIN, 14));

        // Dibujar el contenido del ZPL en la imagen
        int x = 20;
        int y = 20;
        int lineHeight = graphics.getFontMetrics().getHeight();

        // Dividir el contenido del ZPL en líneas para dibujarlo
        String[] lines = zplContent.split("\n");
        for (String line : lines) {
            graphics.drawString(line, x, y);
            y += lineHeight;
        }

        // Liberar el contexto gráfico
        graphics.dispose();

        return image;
    }
}
