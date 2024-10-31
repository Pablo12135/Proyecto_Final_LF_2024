package Reportes;

import java.util.ArrayList;
import java.util.List;


public class ReporteTablasEncontradas {

    private List<String> tablas;
    private List<Integer> lineas;
    private List<Integer> columnas;

    public ReporteTablasEncontradas() {
        tablas = new ArrayList<>();
        lineas = new ArrayList<>();
        columnas = new ArrayList<>();
    }

    public void agregarTabla(String tabla, int linea, int columna) {
        tablas.add(tabla);
        lineas.add(linea);
        columnas.add(columna);
    }

    public void mostrarReporte() {
        System.out.println("Tablas Encontradas:");
        for (int i = 0; i < tablas.size(); i++) {
            System.out.printf("Tabla: %s, Línea: %d, Columna: %d%n",
                    tablas.get(i), lineas.get(i), columnas.get(i));
        }
    }
}
