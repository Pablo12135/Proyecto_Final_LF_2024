package Reportes;

import javax.swing.*;
import java.util.List;

public class Reporte_Error_Sintactico {

    private List<ErrorSintatico> erroressi; // Lista de errores

    // Constructor modificado para aceptar una lista de errores
    public Reporte_Error_Sintactico(List<ErrorSintatico> errores) {
        this.erroressi = errores;
    }

    // Método para agregar un error a la lista (opcional, ya que se pasa la lista al constructor)
    public void agregarError(String token, int linea, int columna, String descripcion) {
        erroressi.add(new ErrorSintatico(token, linea, columna, descripcion));
        System.out.println("Error agregado: " + token); // Mensaje de depuración
    }

    // Método para generar el reporte y mostrarlo en una ventana
    public void generarReporte() {
        // Crear el panel del reporte de errores
        Reporte_error_Sintactico_Panel reportePanel = new Reporte_error_Sintactico_Panel(erroressi);

        // Mostrar el panel en una ventana
        JOptionPane.showMessageDialog(null, reportePanel, "Reporte de Errores Sintacticos", JOptionPane.PLAIN_MESSAGE);
    }
}
