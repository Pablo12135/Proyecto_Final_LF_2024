package Clases_Utilizar;

import java.util.ArrayList;
import java.util.List;

public class Tabla {

    private String nombre; // Nombre de la tabla
    private List<Columna> columnas; // Lista de columnas de la tabla

    public Tabla(String nombre) {
        this.nombre = nombre;
        this.columnas = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public List<Columna> getColumnas() {
        return columnas;
    }

    public void agregarColumna(Columna columna) {
        columnas.add(columna);
    }

    // Método para representar la tabla como String (opcional)
    @Override
    public String toString() {
        return "Tabla{"
                + "nombre='" + nombre + '\''
                + ", columnas=" + columnas
                + '}';
    }
}
