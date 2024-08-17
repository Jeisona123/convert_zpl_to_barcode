import tkinter as tk
from tkinter import filedialog, messagebox
from pathlib import Path
from Reading_and_Processing_ZPL_File import convert_zpl_to_pdf  # Asegúrate de que esta importación esté correcta

def select_zpl_file():
    zpl_file = filedialog.askopenfilename(filetypes=[("ZPL files", "*.zpl")])
    zpl_entry.delete(0, tk.END)
    zpl_entry.insert(0, zpl_file)

def generate_pdf():
    zpl_file = zpl_entry.get()
    output_dir = Path(output_dir_entry.get())
    
    if not zpl_file or not output_dir.is_dir():
        messagebox.showerror("Error", "Please select a valid ZPL file and output directory.")
        return
    
    pdf_output = str(output_dir / "output.pdf")  # Convertir a cadena
    
    try:
        convert_zpl_to_pdf(zpl_file, pdf_output)
        messagebox.showinfo("Success", f"PDF generated at {pdf_output}")
    except Exception as e:
        messagebox.showerror("Error", str(e))

# Crear la ventana principal
root = tk.Tk()
root.title("ZPL to PDF Converter")

# Campos de selección
tk.Label(root, text="ZPL File:").grid(row=0, column=0, padx=10, pady=10)
zpl_entry = tk.Entry(root, width=50)
zpl_entry.grid(row=0, column=1, padx=10, pady=10)
tk.Button(root, text="Browse", command=select_zpl_file).grid(row=0, column=2, padx=10, pady=10)

tk.Label(root, text="Output Directory:").grid(row=1, column=0, padx=10, pady=10)
output_dir_entry = tk.Entry(root, width=50)
output_dir_entry.grid(row=1, column=1, padx=10, pady=10)
tk.Button(root, text="Browse", command=lambda: output_dir_entry.insert(0, filedialog.askdirectory())).grid(row=1, column=2, padx=10, pady=10)

# Botón de generación de PDF
tk.Button(root, text="Generate PDF", command=generate_pdf).grid(row=2, column=1, padx=10, pady=20)

# Ejecutar la aplicación
root.mainloop()
