package com.nirsa;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ZplToPdfConverter {

    public static void main(String[] args) {
        String inputDirectory = "C:\\Proyectos_software\\Nirsa\\Backend\\Doc";
        String outputDirectory = "C:\\Proyectos_software\\Nirsa\\Backend\\Pdf";
        String inputFileName = "zebracode.zpl";
        String outputFileName = "zebracode.pdf";

        String inputPath = Paths.get(inputDirectory, inputFileName).toString();
        String outputPath = Paths.get(outputDirectory, outputFileName).toString();

        try {
            String zplContent = new String(Files.readAllBytes(Paths.get(inputPath)));
            System.out.println("Contenido del archivo ZPL:");
            System.out.println(zplContent);

            if (zplContent.isEmpty()) {
                System.out.println("El archivo ZPL está vacío.");
                return;
            }

            convertZplToPdf(zplContent, outputPath);
            System.out.println("PDF generado exitosamente en: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error al procesar el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void convertZplToPdf(String zplContent, String outputPath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Añadir título
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("ZPL Content Conversion");
                contentStream.endText();

                // Procesar el contenido ZPL
                String[] lines = zplContent.split("\n");
                float y = 730;
                for (String line : lines) {
                    System.out.println("Procesando línea: " + line);
                    if (line.contains("^FD")) {
                        String text = line.substring(line.indexOf("^FD") + 3);
                        if (text.contains("^FS")) {
                            text = text.substring(0, text.indexOf("^FS"));
                        }
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA, 10);
                        contentStream.newLineAtOffset(50, y);
                        contentStream.showText(text);
                        contentStream.endText();
                        y -= 15;
                        System.out.println("Añadido texto: " + text);
                    } if (line.contains("^BC")) {
                        System.out.println("bbbbbbbbb");
                        // Generar código de barras
                        String barcodeData = extractBarcodeData(lines, line);
                        if (!barcodeData.isEmpty()) {
                            BufferedImage barcodeImage = generateBarcode(barcodeData);
                            PDImageXObject pdImage = LosslessFactory.createFromImage(document, barcodeImage);
                            contentStream.drawImage(pdImage, 50, y - 50, 200, 50);
                            y -= 60;
                            System.out.println("Añadido código de barras: " + barcodeData);
                        }
                    }  if (line.contains("^GB")) {
                        // Dibujar una línea
                        String[] parts = line.split(",");
                        if (parts.length >= 3) {
                            float width = Float.parseFloat(parts[1].replaceAll("[^0-9.]", ""));
                            float height = Float.parseFloat(parts[2].replaceAll("[^0-9.]", ""));
                            contentStream.moveTo(50, y);
                            contentStream.lineTo(50 + width, y);
                            contentStream.stroke();
                            y -= height + 5;
                            System.out.println("Añadida línea");
                        }
                    }
                }

                // Si no se ha añadido ningún contenido, agregar un mensaje
                if (y == 730) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.newLineAtOffset(50, y);
                    contentStream.showText("No se pudo extraer contenido adicional del archivo ZPL.");
                    contentStream.endText();
                }
            }

            document.save(outputPath);
        }
    }

    private static String extractBarcodeData(String[] lines, String currentLine) {
        int currentIndex = java.util.Arrays.asList(lines).indexOf(currentLine);
        for (int i = currentIndex; i < lines.length; i++) {
            if (lines[i].contains("^FD")) {
                String data = lines[i].substring(lines[i].indexOf("^FD") + 3);
                if (data.contains("^FS")) {
                    data = data.substring(0, data.indexOf("^FS"));
                }
                System.out.println("Datos de código de barras extraídos: " + data);
                return data;
            }
            if (lines[i].contains("^FS")) {
                break;
            }
        }
        System.out.println("No se encontraron datos de código de barras");
        return "";
    }

    private static BufferedImage generateBarcode(String barcodeData) throws IOException {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(barcodeData, BarcodeFormat.CODE_128, 200, 50);
            BufferedImage image = new BufferedImage(200, 50, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 50; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return image;
        } catch (WriterException e) {
            throw new IOException("Error al generar el código de barras", e);
        }
    }
}