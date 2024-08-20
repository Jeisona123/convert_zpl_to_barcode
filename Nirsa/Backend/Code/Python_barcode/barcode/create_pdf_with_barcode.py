from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas
from barcode import Code128
from barcode.writer import ImageWriter
import os

# Rutas de entrada y salida
zpl_file_path = r'C:\Proyectos_software\Nirsa\Backend\Doc\zebracode.zpl'
img_output_path = r'C:\Proyectos_software\Nirsa\Backend\Img\barcode_output.png'
pdf_output_path = r'C:\Proyectos_software\Nirsa\Backend\Pdf\barcode_output.pdf'

# Función para extraer el dato del código de barras del archivo ZPL
def extract_barcode_data(zpl):
    # Asumiendo que el código de barras está definido con ^FD y termina con ^FS
    start = zpl.find('^FD') + 3
    end = zpl.find('^FS', start)
    return zpl[start:end]

# Leer el archivo ZPL
with open(zpl_file_path, 'r') as file:
    zpl_content = file.read()

# Extraer el código de barras
barcode_data = extract_barcode_data(zpl_content)
print(f"Data de código de barras extraída: {barcode_data}")

# Intenta guardar una imagen de código de barras
try:
    barcode = Code128(barcode_data, writer=ImageWriter())
    image_full_path = barcode.save(img_output_path)  # Esta es la ruta con la extensión adicional .png

    # Verifica si la imagen se guardó correctamente
    if os.path.exists(image_full_path):
        print(f"Imagen guardada correctamente en: {image_full_path}")
    else:
        print("Error al guardar la imagen, la ruta no existe.")
except Exception as e:
    print(f"Ocurrió un error al intentar guardar la imagen: {e}")

# Crear el PDF utilizando la imagen generada
def create_pdf_with_barcode(image_path, pdf_path):
    if os.path.exists(image_path):
        c = canvas.Canvas(pdf_path, pagesize=letter)
        c.drawImage(image_path, 100, 550, 500, 100)  # Ajustar las coordenadas y tamaño si es necesario
        c.showPage()
        c.save()
        print(f"PDF generado correctamente en: {pdf_path}")
    else:
        print("La imagen no se encuentra en la ruta especificada.")

# Solo generar el PDF si la imagen fue guardada correctamente
if os.path.exists(image_full_path):
    create_pdf_with_barcode(image_full_path, pdf_output_path)
