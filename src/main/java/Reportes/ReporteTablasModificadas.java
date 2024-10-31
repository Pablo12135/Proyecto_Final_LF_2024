package Reportes;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ReporteTablasModificadas {

    private List<String> tablas;
    private List<String> modificaciones;
    private List<Integer> lineas;
    private List<Integer> columnas;

    public ReporteTablasModificadas() {
        tablas = new ArrayList<>();
        modificaciones = new ArrayList<>();
        lineas = new ArrayList<>();
        columnas = new ArrayList<>();
    }

    public void agregarModificacion(String tabla, String tipoModificacion, int linea, int columna) {
        tablas.add(tabla);
        modificaciones.add(tipoModificacion);
        lineas.add(linea);
        columnas.add(columna);
    }

    public void mostrarReporte() {
        // Crear un StringBuilder para almacenar el reporte
        StringBuilder reporte = new StringBuilder("Tablas Modificadas:\n");

        // Llenar el reporte con la información de tablas modificadas
        for (int i = 0; i < tablas.size(); i++) {
            reporte.append(String.format("Tabla: %s, Modificación: %s, Línea: %d, Columna: %d%n",
                    tablas.get(i), modificaciones.get(i), lineas.get(i), columnas.get(i)));
        }

        // Mostrar el reporte en un cuadro de diálogo emergente
        JOptionPane.showMessageDialog(null, reporte.toString(), "Reporte de Tablas Modificadas", JOptionPane.INFORMATION_MESSAGE);
    }
}
