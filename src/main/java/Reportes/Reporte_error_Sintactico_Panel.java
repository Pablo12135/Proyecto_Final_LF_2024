package Reportes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Reporte_error_Sintactico_Panel extends JPanel {

    private final List<ErrorSintatico> erroressi; // Lista de errores

    public Reporte_error_Sintactico_Panel(List<ErrorSintatico> errores) {
        this.erroressi = errores;
        inicializarPanel(); // Inicializar el panel cuando se crea la instancia
    }

    // Método para inicializar el panel y añadir los componentes del reporte
    private void inicializarPanel() {
        setLayout(new BorderLayout());

        // Definir las columnas de la tabla
        String[] columnas = {"Token", "Linea", "Columna", "Descripcion"};

        // Crear un modelo de tabla con las columnas definidas
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);

        // Añadir cada error como una fila en la tabla
        for (ErrorSintatico error : erroressi) {
            modeloTabla.addRow(new Object[]{
                error.getToken(),
                error.getLinea(),
                error.getColumna(),
                error.getDescripcion()
            });
        }

        // Crear una JTable con el modelo de datos
        JTable tablaErrores = new JTable(modeloTabla);

        // Hacer que la tabla se ajuste al tamaño del panel
        tablaErrores.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Crear un JScrollPane para permitir el desplazamiento en la tabla
        JScrollPane scrollPane = new JScrollPane(tablaErrores);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Añadir el JScrollPane al centro del panel
        add(scrollPane, BorderLayout.CENTER);

        // Botón para volver al inicio (esto puede no ser necesario en este contexto, ya que no hay desplazamiento por el texto)
        JButton volverAlInicioButton = new JButton("Volver al Inicio");
        volverAlInicioButton.addActionListener(e -> tablaErrores.scrollRectToVisible(new Rectangle(0, 0, 1, 1))); // Desplazar al inicio de la tabla

        // Panel para contener el botón
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(volverAlInicioButton);

        // Añadir el panel de botones al sur
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
