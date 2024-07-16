#-------------------------------------------------------------------------------
# Name:        module1
# Purpose:
#
# Author:      9A4AM
#
# Created:     16.07.2024
# Copyright:   (c) 9A4AM 2024
# Licence:     <your licence>
#-------------------------------------------------------------------------------

import tkinter as tk
from tkinter import messagebox

def izracunaj_uzgon():
    try:
        masa_adaptera = float(entry_masa_adaptera.get())
        masa_konopca = float(entry_masa_konopca.get())
        masa_sonde = float(entry_masa_sonde.get())
        zeljeni_uzgon = float(entry_zeljeni_uzgon.get())

        uzgon_na_vagi = (masa_sonde + masa_konopca + zeljeni_uzgon) - masa_adaptera

        messagebox.showinfo("Rezultat", f"Uzgon na vagi: {uzgon_na_vagi} grama")
    except ValueError:
        messagebox.showerror("Greška", "Molimo unesite validne numeričke vrednosti")

# Kreiraj glavni prozor
root = tk.Tk()
root.title("Izračunavanje uzgona za meteo balone")

# Dodaj labele i unosna polja
tk.Label(root, text="Masa adaptera (g):").grid(row=0, column=0, padx=10, pady=5)
entry_masa_adaptera = tk.Entry(root)
entry_masa_adaptera.grid(row=0, column=1, padx=10, pady=5)

tk.Label(root, text="Masa konopca (g):").grid(row=1, column=0, padx=10, pady=5)
entry_masa_konopca = tk.Entry(root)
entry_masa_konopca.grid(row=1, column=1, padx=10, pady=5)

tk.Label(root, text="Masa sonde (g):").grid(row=2, column=0, padx=10, pady=5)
entry_masa_sonde = tk.Entry(root)
entry_masa_sonde.grid(row=2, column=1, padx=10, pady=5)

tk.Label(root, text="Željeni uzgon (g):").grid(row=3, column=0, padx=10, pady=5)
entry_zeljeni_uzgon = tk.Entry(root)
entry_zeljeni_uzgon.grid(row=3, column=1, padx=10, pady=5)

# Dodaj dugme za izračunavanje
tk.Button(root, text="Izračunaj uzgon", command=izracunaj_uzgon).grid(row=4, columnspan=2, pady=10)

# Pokreni glavni loop
root.mainloop()
