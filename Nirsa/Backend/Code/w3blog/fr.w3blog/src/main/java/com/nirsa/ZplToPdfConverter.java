package com.nirsa;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ZplToPdfConverter {

    public static void main(String[] args) {
        String zplFilePath = "C:\\Proyectos_software\\Nirsa\\Backend\\Doc\\zebracode.zpl";
        String pdfOutputPath = "C:\\Proyectos_software\\Nirsa\\Backend\\pdf\\zebracode.pdf";

        try {
            // Leer el contenido del archivo ZPL
            String zplContent = new String(Files.readAllBytes(Paths.get(zplFilePath)));

            // Crear un nuevo documento PDF
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            // Crear un contenido para la página
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Establecer el punto de inicio para escribir texto
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(100, 700);

            // Dividir el contenido en líneas y escribir cada una por separado
            String[] lines = zplContent.split("\r\n|\r|\n");
            for (String line : lines) {
                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -14); // Mover a la siguiente línea
            }

            contentStream.endText();
            contentStream.close();

            // Guardar el documento PDF
            document.save(pdfOutputPath);
            document.close();

            System.out.println("PDF generado correctamente en: " + pdfOutputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

