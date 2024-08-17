package com.nirsa;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ZplToPdfConverter {

    public static void main(String[] args) {
        String zplFilePath = "C:\\Proyectos_software\\Nirsa\\Backend\\Doc\\zebracode.zpl";
        String outputPdfPath = "C:\\Proyectos_software\\Nirsa\\Backend\\Pdf\\zebracodeV3.Pm5.pdf";

        try {
            // Leer el archivo ZPL
            String zplContent = new String(Files.readAllBytes(Paths.get(zplFilePath)));

            // Extraer el código de barras del contenido del archivo ZPL
            String barcodeData = extractBarcodeData(zplContent);

            // Generar la imagen del código de barras
            BufferedImage barcodeImage = generateBarcodeImage(barcodeData);

            // Crear el PDF e insertar la imagen del código de barras
            createPdfWithBarcode(barcodeImage, outputPdfPath);

            System.out.println("PDF generado correctamente en: " + outputPdfPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String extractBarcodeData(String zplContent) {
        // Buscar la sección del código de barras en el contenido ZPL
        String barcodeData = null;
        String[] lines = zplContent.split("\\^");

        for (int i = 0; i < lines.length; i++) {
            // Buscar la línea que contiene ^BC, que indica un código de barras Code 128
            if (lines[i].startsWith("BC")) {
                // La línea siguiente después de ^BC debe contener el dato del código de barras después de ^FD
                if (lines[i + 1].startsWith("FD")) {
                    barcodeData = lines[i + 1].substring(2).trim();
                    break;
                }
            }
        }

        return barcodeData;
    }

    private static BufferedImage generateBarcodeImage(String barcodeData) throws IOException {
        Code128Bean barcodeGenerator = new Code128Bean();
        final int dpi = 150;  // Dots per inch (Puntos por pulgada)

        // Configurar el generador de código de barras
        double moduleWidth = 1.0 / 5.0;  // Basado en ^BY5, el ancho del módulo es 1/5 de pulgada
        barcodeGenerator.setModuleWidth(moduleWidth);  // Ajustar la magnificación del ancho del módulo
        barcodeGenerator.setBarHeight(15);  // Altura del código de barras según ^BY5,2,270
//        barcodeGenerator.setHeight(15);
        barcodeGenerator.doQuietZone(false);  // Quiet zone deshabilitado

        // Crear la imagen del código de barras
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        barcodeGenerator.generateBarcode(canvas, barcodeData);
        canvas.finish();

        return canvas.getBufferedImage();
    }

    private static void createPdfWithBarcode(BufferedImage barcodeImage, String outputPdfPath) throws IOException {
        // Crear el PdfWriter y el PdfDocument
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputPdfPath));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Agregar contenido del archivo ZPL al PDF
        document.add(new Paragraph("Contenido del archivo ZPL:"));
        document.add(new Paragraph("Código de Barras Generado:\n"));

        // Convertir la imagen del código de barras en un objeto Image de iText 7
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(barcodeImage, "png", baos);
        Image barcodePdfImage = new Image(com.itextpdf.io.image.ImageDataFactory.create(baos.toByteArray()));
        document.add(barcodePdfImage);

        // Cerrar el documento
        document.close();
    }
}
