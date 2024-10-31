package Reportes;

import javax.swing.*;
import java.util.List;

public class Reporte_Error_Lexico {

    private List<ErrorLexico> errores; // Lista de errores

    // Constructor modificado para aceptar una lista de errores
    public Reporte_Error_Lexico(List<ErrorLexico> errores) {
        this.errores = errores;
    }

    // Método para agregar un error a la lista (opcional, ya que se pasa la lista al constructor)
    public void agregarError(String token, int linea, int columna, String descripcion) {
        errores.add(new ErrorLexico(token, linea, columna, descripcion));
        System.out.println("Error agregado: " + token); // Mensaje de depuración
    }

    // Método para generar el reporte y mostrarlo en una ventana
    public void generarReporte() {
        // Crear el panel del reporte de errores
        Reporte_Error_lexico_Panel reportePanel = new Reporte_Error_lexico_Panel(errores);

        // Mostrar el panel en una ventana
        JOptionPane.showMessageDialog(null, reportePanel, "Reporte de Errores", JOptionPane.PLAIN_MESSAGE);
    }
}
