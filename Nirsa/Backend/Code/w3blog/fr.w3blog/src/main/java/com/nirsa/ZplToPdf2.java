import com.nirsa.ZebraLabel;
import com.nirsa.ZebraText;
import fr.w3blog.zpl.model.ZebraLabel;
import fr.w3blog.zpl.model.element.ZebraText;
import net.sf.jzebra.ZebraPrinter; // Replace with actual library

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.rendering.PDFRenderer;

public class ZplToPdf2 {

    public static void main(String[] args) throws IOException {
        String zplFilePath = "C:\\Proyectos_software\\Nirsa\\Backend\\Doc\\zebracode.zpl";
        String pdfOutputPath = "C:\\Proyectos_software\\Nirsa\\Backend\\Pdf\\zebracode2.pdf";

        // Read ZPL content
        String zplContent = new String(Files.readAllBytes(Paths.get(zplFilePath)));

        // Create ZebraLabel
        ZebraLabel zebraLabel = new ZebraLabel(912, 612);
        zebraLabel.addElement(new ZebraText(10, 10, zplContent));

        // Generate image from ZPL (replace with actual method from your library)
        BufferedImage image = ZebraPrinter.getZPLImage(zplContent, 300); // Adjust DPI as needed

        // Create PDF document
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(doc, page);
        contentStream.drawImage(image, 0, 0); // Adjust coordinates as needed
        contentStream.close();

        // Save PDF
        doc.save(pdfOutputPath);
        doc.close();

        System.out.println("PDF generated correctly in: " + pdfOutputPath);
    }
}
