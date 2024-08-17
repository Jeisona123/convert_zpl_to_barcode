package com.nirsa;

import fr.w3blog.zpl.model.ZebraLabel;
import fr.w3blog.zpl.model.element.ZebraText;
import fr.w3blog.zpl.utils.ZebraImageUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ZplToPdf {

    public static void main(String[] args) {
        String zplFilePath = "C:\\Proyectos_software\\Nirsa\\Backend\\Doc\\zebracode.zpl";
        String pdfOutputPath = "C:\\Proyectos_software\\Nirsa\\Backend\\Pdf\\zebracode2.pdf";

        try {
            // Leer el contenido del archivo ZPL
            String zplContent = new String(Files.readAllBytes(Paths.get(zplFilePath)));

            // Crear una instancia de ZebraLabel manualmente (ya que no hay un método para crear desde ZPL)
            ZebraLabel zebraLabel = new ZebraLabel(912, 612); // Define el tamaño de la etiqueta
            zebraLabel.addElement(new ZebraText(10, 10, zplContent)); // Añadir el texto ZPL como contenido

            // Generar el archivo PDF
            File pdfFile = new File(pdfOutputPath);
            ZebraImageUtils.saveImage(zebraLabel, pdfFile);

            System.out.println("PDF generado correctamente en: " + pdfFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
