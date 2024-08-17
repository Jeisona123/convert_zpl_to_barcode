from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas

def convert_zpl_to_pdf(zpl_file, pdf_output):
    # Leer el archivo ZPL
    with open(zpl_file, 'r') as file:
        zpl_data = file.read()
    
    # Inicializar canvas de PDF
    c = canvas.Canvas(pdf_output, pagesize=letter)
    
    # Aquí, podrías parsear el ZPL manualmente y dibujar el contenido en el PDF.
    # Este es un ejemplo simple que dibuja un texto en el PDF:
    c.drawString(100, 750, "Este es un ejemplo de texto extraído del ZPL.")
    
    # Guardar el PDF
    c.save()

# Ejemplo de uso:
zpl_file_path = r"C:\Proyectos_software\Nirsa\Backend\Doc\zebracode.zpl"
pdf_output_path = r"C:\Proyectos_software\Nirsa\Backend\pdf\output.pdf"
convert_zpl_to_pdf(zpl_file_path, pdf_output_path)
