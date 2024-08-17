import requests
from pathlib import Path
from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas

# Ruta del archivo ZPL
zpl_file_path = Path(r"C:\Proyectos_software\Nirsa\Backend\Doc\zebracode.zpl")
# Ruta de salida para la imagen y PDF
image_output_path = Path(r"C:\Proyectos_software\Nirsa\Backend\pdf\label_image.png")
pdf_output_path = Path(r"C:\Proyectos_software\Nirsa\Backend\pdf\output.pdf")

def convert_zpl_to_image(zpl_file, image_output):
    # Leer el contenido del archivo ZPL
    with open(zpl_file, 'r') as file:
        zpl_data = file.read()

    # Hacer la solicitud a la API de Labelary para obtener un PNG
    url = 'http://api.labelary.com/v1/printers/8dpmm/labels/4x6/0/'
    headers = {
        'Accept': 'image/png',  # Cambiar a 'image/png'
    }
    response = requests.post(url, headers=headers, data=zpl_data)

    # Verificar si la solicitud fue exitosa
    if response.status_code == 200:
        # Guardar la imagen en el archivo de salida
        with open(image_output, 'wb') as f:
            f.write(response.content)
        print(f"Imagen guardada en: {image_output}")
    else:
        print(f"Error: {response.status_code} - {response.text}")
        raise Exception(f"Failed to convert ZPL to image: {response.status_code} - {response.text}")

def create_pdf_from_image(image_path, pdf_output):
    # Crear el PDF
    c = canvas.Canvas(str(pdf_output), pagesize=letter)
    # Asegurarse de que la imagen es un PNG válido
    c.drawImage(str(image_path), 0, 0, width=letter[0], height=letter[1])
    c.save()
    print(f"PDF guardado en: {pdf_output}")

# Convertir ZPL a imagen
convert_zpl_to_image(zpl_file_path, image_output_path)

# Crear el PDF a partir de la imagen
create_pdf_from_image(image_output_path, pdf_output_path)
