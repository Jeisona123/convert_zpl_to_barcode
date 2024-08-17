import os
from PIL import Image, ImageDraw, ImageFont
from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas

# Definir las rutas de los archivos
input_path = r'C:\Proyectos_software\Nirsa\Backend\Doc\zebracode.zpl'
output_dir = r'C:\Proyectos_software\Nirsa\Backend\Pdf'
output_path = os.path.join(output_dir, 'zebracode.pdf')

# Verificar si la carpeta de salida existe, si no, crearla
os.makedirs(output_dir, exist_ok=True)
print(f"Directorio de salida verificado/creado: {output_dir}")

# Leer el archivo ZPL
try:
    with open(input_path, 'r') as file:
        zpl_data = file.read()
        print("Archivo ZPL leído correctamente.")
except FileNotFoundError:
    print(f"Archivo ZPL no encontrado en la ruta: {input_path}")
    exit(1)

# Crear un nuevo PDF
c = canvas.Canvas(output_path, pagesize=letter)
width, height = letter

# Ejemplo básico de renderizado manual
# Crear una imagen temporal para el código de barras
img = Image.new('RGB', (600, 300), color=(255, 255, 255))
d = ImageDraw.Draw(img)

# Parsear y dibujar el código de barras simple (esto es solo un ejemplo)
# Buscar el comando de código de barras (simulado)
if '^FO50,50' in zpl_data and '^GB100,100,100' in zpl_data:
    # Dibujar un cuadro simple en la imagen
    d.rectangle([50, 50, 150, 150], outline=(0, 0, 0), width=2)

# Guardar la imagen como un archivo temporal
temp_img_path = os.path.join(output_dir, 'temp_barcode.png')
img.save(temp_img_path)

# Agregar la imagen al PDF
c.drawImage(temp_img_path, 100, height - 250, width=200, height=100)

# Agregar el contenido del ZPL como texto para referencia
c.drawString(100, height - 300, "ZPL Content:")
c.drawString(100, height - 320, zpl_data[:500])

# Finalizar el PDF
c.showPage()
c.save()

# Eliminar la imagen temporal
os.remove(temp_img_path)

print(f"PDF guardado en: {output_path}")
