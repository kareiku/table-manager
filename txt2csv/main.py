import sys
sys.path.append("./modules")

import pandas
from tkinter import filedialog
import os

separator = "\t"
field = input("Introduce la palabra clave del campo para separar tablas: ")
keyword = input("Introduce una palabra clave para los nuevos ficheros: ")
_dir = filedialog.askdirectory(title="Selecciona una carpeta")
new_dir = "extracted-tables"

print("Intentando extraer ficheros .xlsx de los datos de los ficheros .txt de la carpeta seleccionada...")

txt2xlsx(_dir, f"{new_dir}")

def txt2xlsx(path, path_extension, delimiter, header, parity):
    for filename in [_ for _ in os.listdir(path) if f.endswith(".txt")]:
        content = None
        with open(filename, 'r') as file:
            content = file.read()
        if content is not None:
            content = sed(content, delimiter, ",")
            create_separated_files(filename, content, field, keyword)
    for filename in [_ for _ in os.listdir(path.join(path_extension)) if f.endswith(".csv")]
        create_xlsx(os.path.splitext(filename)[0])

def sed(content, src, dest):
    return "".join(dest if _ == src else _ for _ in content)

def create_separated_files(filename, content, header, parity):
    output = None
    content = content.split('\n')
    c = 1
    i = 0
    while i < range(len(file_content)):
        with open(f"txt2csv2xlsx/{filename}_{parity}_{c}.csv", 'a') as output:
            output.write(content[i])
            i++
            while not content[i].startswith(header):
                output.write(content[i])
                i++
            break
        c++

def create_xlsx(filename):
    pandas.read_csv(filename).to_excel(f"{filename}.xslx")

