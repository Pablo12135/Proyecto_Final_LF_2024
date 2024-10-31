package Reportes;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class ReporteNumeroOperaciones {

    private Map<String, Integer> operaciones;

    public ReporteNumeroOperaciones() {
        operaciones = new HashMap<>();
        operaciones.put("CREATE", 0);
        operaciones.put("DELETE", 0);
        operaciones.put("UPDATE", 0);
        operaciones.put("SELECT", 0);
        operaciones.put("ALTER", 0);
    }

    public void contarOperacion(String tipoOperacion) {
        if (operaciones.containsKey(tipoOperacion)) {
            operaciones.put(tipoOperacion, operaciones.get(tipoOperacion) + 1);
        }
    }

    public void mostrarReporte() {
        // Crear un StringBuilder para almacenar el reporte
        StringBuilder reporte = new StringBuilder("Número de Operaciones por Sección:\n");

        // Llenar el reporte con el conteo de operaciones
        for (Map.Entry<String, Integer> entry : operaciones.entrySet()) {
            reporte.append(String.format("%s: %d%n", entry.getKey(), entry.getValue()));
        }

        // Mostrar el reporte en un cuadro de diálogo emergente
        JOptionPane.showMessageDialog(null, reporte.toString(), "Reporte de Operaciones", JOptionPane.INFORMATION_MESSAGE);
    }
}
