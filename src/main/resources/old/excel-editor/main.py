import sys
sys.path.insert(0, "./modules")

from tkinter import Tk, PhotoImage, filedialog, Toplevel, StringVar, Menu, Frame
from tkinter.ttk import OptionMenu, Button, Entry, Style, Label
from tkinter.font import Font
from tksheet import Sheet
from pandas import read_excel, ExcelFile
from datetime import datetime

IMGDATA = """iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAHrSURBVFhHYxhowAilcQOXCQJQFibYU/AByiIb4HaAywQDIDkfiBWAGLcjGBg2AHEiuY7B7gCI5fuBGJ/FyOABEBuS4whcDgBZ7pDjr89QH2fOIMTLARFHAv/+/Wdg9ZgM5YEBWY5ggtLowAFEFIcaMYjwcTIwMTJiYiaE2xXE+cAUEJ/Hm2awAEwHIBkgJ8YLZeEHZ6dHMhgoi4KYJDsCVwiQBAR42Bn29wST5QiqOAAEyHUE1RwAAuQ4gqoOAAFSHUG2A0A5gY+LDcy+cPc1w7///+GYj5uNYW93EFGOwCwHIArfg5h/d+WBLcIF8qceZJi04QKURxBsAJYRgVA2HFAUBROz7RnyAkCFJlEgAEqjAIpCABmAgh4bQCsxBdFLSqolQngJiY6RSkxsgGgHICcyQpgUQHQU+NZuYthy4j6YTQiAUv/5GVFgNshBzG6TwGwgID8Knrz+AmURBjKiPFAWYUBSIiQleGH6qBYCIABPWERgYsFoIhxNhIMwESK58MOXn1AW+eDd5x9QFhCg+R4EcIXAARAR3LiV4deff/DUTSoGeaB4xmGwgUAA6sBgAOxh5TIB1IggLskTB0A+DwSGANhjyAB7COwpgHQycLiaBACyGGSpIzbLQYC41ALJGeQBLPE+iAADAwC2vikzyAvhEAAAAABJRU5ErkJggg=="""
COLOR = "#004490"

current_sort_order = True

class Main:
    def __init__(self):
        self.root = Tk()
        self.root.state('zoomed')
        self.root.resizable(True, True)
        self.root.minsize(500, 300)
        self.root.title("Visualizador Excel")
        self.root.iconphoto(False, PhotoImage(data=IMGDATA))
        self.root.configure(background=COLOR)
        style = Style()
        style.theme_use("clam")
        style.configure(".", font=Font(family='Arial', size=11))

        self.first_column_var = StringVar(self.root)
        self.second_column_var = StringVar(self.root)
        self.filter_var = StringVar(self.root)
        self.second_filter_var = StringVar(self.root)
        self.sort_var = StringVar(self.root)

        mainmenu = Menu(self.root)
        self.root.config(menu=mainmenu)
        submenu = Menu(mainmenu, tearoff=False)
        mainmenu.add_cascade(label="Archivo", menu=submenu)
        submenu.add_command(label="Abrir", command=self.open_file_dialog)
        submenu.add_command(label="Exportar", command=self.export_filtered_data)

        self.frame = Frame(self.root)
        self.frame.pack(side="top", fill="x")
        self.frame.configure(background=COLOR)

        self.filter_label = Label(self.frame, text="Filtros:", background=COLOR, foreground="#fff", font=('Arial', 11, 'bold'))
        self.filter_label.pack(side='left', padx=5, pady=10)

        self.first_column_menu = OptionMenu(self.frame, self.first_column_var, "")
        self.first_column_menu.pack(side='left', padx=5, pady=10)

        self.filter_entry = Entry(self.frame, textvariable=self.filter_var)
        self.filter_entry.pack(side='left', padx=5, pady=10)
        self.filter_entry.bind("<KeyRelease>", self.filter_table)

        self.second_column_menu = OptionMenu(self.frame, self.second_column_var, "")
        self.second_column_menu.pack(side='left', padx=5, pady=10)

        self.second_filter_entry = Entry(self.frame, textvariable=self.second_filter_var)
        self.second_filter_entry.pack(side='left', padx=5, pady=10)
        self.second_filter_entry.bind("<KeyRelease>", self.filter_table)

        self.sort_menu = OptionMenu(self.frame, self.sort_var, "Ordenar por...")
        self.sort_menu.pack(side='left', padx=5, pady=10)

        self.sort_button_up = Button(self.frame, text="Ordenar", command=lambda: self.sort_table())
        self.sort_button_up.pack(side='left', padx=5, pady=10)

        self.sheet = Sheet(self.root, page_up_down_select_row=True)
        self.sheet.enable_bindings(("ctrl_select", "select_all", "right_click_popup_menu", "toggle_select", "single_select", "drag_select", "column_select", "row_select", "column_width_resize", "double_click_column_resize", "arrowkeys", "copy"))
        self.sheet.pack(fill="both", expand=True, padx=0, pady=0)

        self.df = None

    def open_file_dialog(self):
        file_path = filedialog.askopenfilename(
            title="Seleccione un archivo",
            filetypes=(("Archivos Excel", [".xls", ".xlsx", ".xlsm"]), ("Todos los archivos", "*.*")))

        if file_path:
            try:
                sheet_selection_dialog = Toplevel(self.root)
                sheet_selection_dialog.title("Seleccionar Hoja")
                sheet_selection_dialog.iconphoto(False, PhotoImage(data=IMGDATA))
                sheet_selection_dialog.transient(self.root)
                sheet_selection_dialog.resizable(False, False)
                sheet_selection_dialog.protocol("WM_DELETE_WINDOW", lambda: exit(-1))

                sheets = ExcelFile(file_path).sheet_names

                sheet_var = StringVar(sheet_selection_dialog)
                sheet_var.set(sheets[0])

                OptionMenu(sheet_selection_dialog, sheet_var, sheets[0], *sheets).grid(column=0, row=0)

                def load_selected_sheet():
                    selected_sheet = sheet_var.get()
                    self.load_data_sheet(file_path, selected_sheet)
                    sheet_selection_dialog.destroy()

                Button(sheet_selection_dialog, text="Cargar", command=load_selected_sheet).grid(column=1, row=0)

            except Exception as ex:
                pass

    def load_data_sheet(self, file_path, sheetname):
        try:
            df = read_excel(file_path, sheet_name=sheetname)
            self.df = df.astype(str)
            self.update_table()
        except Exception as ex:
            pass

    def update_table(self):
        if self.df:
            self.df = self.df.replace({'nan': "", 'NaT': ""})

            for column in self.df.columns:
                for i, value in enumerate(self.df[column]):
                    try:
                        date_obj = datetime.strptime(value, '%Y-%m-%d')
                        self.df.at[i, column] = date_obj.strftime('%d-%m-%Y')
                    except ValueError:
                        try:
                            date_obj = datetime.strptime(
                                value, '%Y-%m-%d %H:%M:%S')
                            self.df.at[i, column] = date_obj.strftime('%d-%m-%Y')
                        except ValueError:
                            pass

            self.sheet.set_sheet_data(self.df.values.tolist())
            self.sheet.headers(self.df.columns.tolist())

            self.first_column_menu['menu'].delete(0, 'end')
            self.second_column_menu['menu'].delete(0, 'end')
            self.sort_menu['menu'].delete(0, 'end')

            for column in self.df.columns:
                column_str = str(column)
                self.first_column_menu['menu'].add_command(label=column_str, command=lambda value=column_str: self.first_column_var.set(value))
                self.second_column_menu['menu'].add_command(label=column_str, command=lambda value=column_str: self.second_column_var.set(value))
                self.sort_menu['menu'].add_command(label=column_str, command=lambda value=column_str: self.sort_var.set(value))

            self.first_column_var.set(str(self.df.columns[0]))
            self.second_column_var.set(str(self.df.columns[0]))
            self.sort_var.set(str(self.df.columns[0]))

            try:
                self.sheet.set_currently_selected(0, 0)
            except TypeError:
                pass

    def filter_table(self, *args):
        filter_text = self.filter_var.get()
        second_filter_text = self.second_filter_var.get()

        if self.df and (filter_text or second_filter_text):
            selected_column = self.first_column_var.get()
            second_selected_column = self.second_column_var.get()

            self.filtered_df = self.df.copy()

            if filter_text:
                self.filtered_df = self.filtered_df[self.filtered_df[selected_column].astype(str).str.contains(filter_text, na=False, case=False)]

            if second_filter_text:
                self.filtered_df = self.filtered_df[self.filtered_df[second_selected_column].astype(str).str.contains(second_filter_text, na=False, case=False)]

            filtered_data = self.filtered_df.values.tolist()
            self.sheet.set_sheet_data(filtered_data, reset_col_positions=True)
        else:
            self.sheet.set_sheet_data(self.df.values.tolist(), reset_col_positions=True)

    def sort_table(self):
        global current_sort_order

        if self.df:
            selected_column = self.sort_var.get()
            filter_text = self.filter_var.get()
            second_filter_text = self.second_filter_var.get()

            if selected_column:
                if filter_text or second_filter_text:
                    if self.filtered_df and not self.filtered_df.empty:
                        self.filtered_df.sort_values(by=selected_column, ascending=current_sort_order, inplace=True)
                        current_sort_order = not current_sort_order
                        sorted_data = self.filtered_df.values.tolist()
                        self.sheet.set_sheet_data(sorted_data, reset_col_positions=True)
                    else:
                        self.df.sort_values(by=selected_column, ascending=current_sort_order, inplace=True)
                        current_sort_order = not current_sort_order
                        sorted_data = self.df.values.tolist()
                        self.sheet.set_sheet_data(sorted_data, reset_col_positions=True)
                else:
                    self.df.sort_values(by=selected_column, ascending=current_sort_order, inplace=True)
                    current_sort_order = not current_sort_order
                    sorted_data = self.df.values.tolist()
                    self.sheet.set_sheet_data(sorted_data, reset_col_positions=True)

    def export_filtered_data(self):
        if self.df:
            filter_text = self.filter_var.get()
            second_filter_text = self.second_filter_var.get()
            selected_column = self.first_column_var.get()
            second_selected_column = self.second_column_var.get()
            if filter_text or second_filter_text:
                filtered_df = self.df.copy()
                if filter_text:
                    filtered_df = filtered_df[filtered_df[selected_column].astype(
                        str).str.contains(filter_text, na=False, case=False)]
                if second_filter_text:
                    filtered_df = filtered_df[filtered_df[second_selected_column].astype(
                        str).str.contains(second_filter_text, na=False, case=False)]
                if not filtered_df.empty:
                    file_path = filedialog.asksaveasfilename(defaultextension=".xlsx", filetypes=[("Excel Files", "*.xlsx")])
                    if file_path:
                        filtered_df.to_excel(file_path, index=False)
            else:
                file_path = filedialog.asksaveasfilename(defaultextension=".xlsx", filetypes=[("Excel Files", "*.xlsx")])
                if file_path:
                    self.df.to_excel(file_path, index=False)

    def start(self):
        self.root.mainloop()


if __name__ == "__main__":
    Main().start()
