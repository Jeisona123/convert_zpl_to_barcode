package com.nirsa;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ZplToPdfConverter {
    public static void main(String[] args) {
        String inputDirectory = "C:\\\\Proyectos_software\\\\Nirsa\\\\Backend\\\\Doc";
        String outputDirectory = "C:\\\\Proyectos_software\\\\Nirsa\\\\Backend\\\\Pdf";
        String inputFileName = "zebracode.zpl";
        String outputFileName = "zebracode.pdf";

        String inputPath = Paths.get(inputDirectory, inputFileName).toString();
        String outputPath = Paths.get(outputDirectory, outputFileName).toString();

        try {
            // Leer el archivo ZPL
            String zplContent = new String(Files.readAllBytes(Paths.get(inputPath)), StandardCharsets.UTF_8);

            // Crear un documento PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            // Procesar líneas de ZPL
            String[] lines = zplContent.split("\\r?\\n");
            for (String line : lines) {
                if (line.contains("^BC")) { // Identifica un comando de código de barras
                    if (line.contains("^FD") && line.contains("^FS")) {
                        // Obtener el contenido del código de barras
                        String barcodeData = line.split("\\^FD")[1].split("\\^FS")[0];

                        // Generar la imagen del código de barras
                        Code128Writer barcodeWriter = new Code128Writer();
                        BitMatrix bitMatrix = barcodeWriter.encode(barcodeData, BarcodeFormat.CODE_128, 300, 100);
                        BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

                        // Convertir la imagen a iText Image
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(barcodeImage, "png", baos);
                        Image iTextImage = Image.getInstance(baos.toByteArray());

                        // Añadir la imagen al PDF
                        document.add(iTextImage);
                    }
                } else if (line.contains("^FO") && line.contains("^FD") && line.contains("^FS")) {
                    // Extraer el texto a añadir
                    String textData = line.split("\\^FD")[1].split("\\^FS")[0];

                    // Añadir el texto al PDF
                    document.add(new Paragraph(textData));
                }
                // Otros comandos ZPL pueden ser añadidos aquí (e.g. líneas, cuadros)
            }



            document.close();
            System.out.println("PDF creado exitosamente en " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

