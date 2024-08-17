package com.nirsa;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ZplToPdfConverter {

    private static final String LABELARY_API_URL = "http://api.labelary.com/v1/printers/8dpmm/labels/4x6/0/";

    public static void main(String[] args) {
        String zplFilePath = "C:\\Proyectos_software\\Nirsa\\Backend\\Doc\\zebracode.zpl";
        String outputPdfPath = "C:\\Proyectos_software\\Nirsa\\Backend\\pdf\\output.pdf";

        try {
            // Leer el contenido del archivo ZPL
            String zplContent = new String(Files.readAllBytes(Paths.get(zplFilePath)));

            // Llamar a la API de Labelary para convertir ZPL a PDF
            byte[] pdfData = convertZplToPdf(zplContent);

            // Verificar si los datos recibidos son válidos
            if (pdfData != null && pdfData.length > 0) {
                // Guardar el PDF en el disco
                try (FileOutputStream fos = new FileOutputStream(outputPdfPath)) {
                    fos.write(pdfData);
                }
                System.out.println("PDF generado correctamente en " + outputPdfPath);
            } else {
                System.out.println("Error: No se recibieron datos válidos para el PDF.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] convertZplToPdf(String zplContent) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(LABELARY_API_URL);

            // Configurar la solicitud HTTP con el ZPL
            StringEntity entity = new StringEntity(zplContent, "UTF-8");
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setHeader("Accept", "application/pdf");

            // Ejecutar la solicitud
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();

                // Verificar que el tipo de contenido sea PDF
                String contentType = responseEntity.getContentType().getValue();
                if (!contentType.equals("application/pdf")) {
                    System.out.println("Error: El tipo de contenido de la respuesta no es PDF, sino: " + contentType);

                    // Imprimir el cuerpo de la respuesta para entender el error
                    String responseBody = EntityUtils.toString(responseEntity);
                    System.out.println("Cuerpo de la respuesta: " + responseBody);

                    return null;
                }

                // Retornar los datos del PDF
                return EntityUtils.toByteArray(responseEntity);
            }
        }
    }
}
