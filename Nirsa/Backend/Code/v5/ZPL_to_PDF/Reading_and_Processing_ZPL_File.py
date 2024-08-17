import os
from zebra import Zebra
from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas

# Ruta del archivo ZPL
zpl_file_path = r"C:\Proyectos_software\Nirsa\Backend\Doc\zebracode.zpl"
# Ruta de salida para el PDF
pdf_output_path = r"C:\Proyectos_software\Nirsa\Backend\pdf\output.pdf"

def convert_zpl_to_pdf(zpl_file, pdf_output):
    # Leer el archivo ZPL
    with open(zpl_file, 'r') as file:
        zpl_data = file.read()
    
    # Inicializar canvas de PDF
    c = canvas.Canvas(pdf_output, pagesize=letter)
    
    # Configurar la página
    width, height = letter
    c.setFont("Helvetica", 10)
    
    # Usar Zebra para interpretar el ZPL y agregarlo al PDF
    z = Zebra()
    z.setup()
    image = z.render(zpl_data)
    
    # Agregar imagen ZPL convertida al PDF
    c.drawImage(image, 0, 0, width, height)
    
    # Guardar el PDF
    c.save()

# Llamar a la función para realizar la conversión
convert_zpl_to_pdf(zpl_file_path, pdf_output_path)
