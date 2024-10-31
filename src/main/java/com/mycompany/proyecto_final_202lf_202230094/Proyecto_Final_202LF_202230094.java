package com.mycompany.proyecto_final_202lf_202230094;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import Reportes.ErrorLexico;
import Reportes.Reporte_Error_Lexico;
import Reportes.ErrorSintatico;
import Reportes.Reporte_Error_Sintactico;
import Reportes.ReporteNumeroOperaciones;
import Reportes.ReporteTablasModificadas;

import Generador_Grafico.Grafica;

import Clases_Utilizar.Analizador_Sintatico;
import Clases_Utilizar.Modificador;
import Clases_Utilizar.Tabla;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Proyecto_Final_202LF_202230094 extends JFrame {

    private JTextPane textPane;
    private JLabel posicionCursor;
    private StyledDocument doc;
    private boolean archivoAbierto = false; // Variable para controlar si un archivo está abierto7
    private List<ErrorLexico> errors;
    private List<ErrorSintatico> errorssi;
    private ReporteNumeroOperaciones reporteNumeroOperaciones;
    private ReporteTablasModificadas reportesmodi;

    public Proyecto_Final_202LF_202230094() {
        // Configurar ventana principal
        setTitle("Editor de Código con Estilos");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar ventana

        // Crear el menú superior
        JMenuBar menuBar = new JMenuBar();
        JMenu archivoMenu = new JMenu("Archivo");
        JMenu reportesMenu = new JMenu("Reportes");
        menuBar.add(archivoMenu);
        menuBar.add(reportesMenu);
        setJMenuBar(menuBar);

        // Crear el panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Crear el JTextPane y su documento para agregar estilos
        textPane = new JTextPane();
        doc = textPane.getStyledDocument();
        JScrollPane scrollPane = new JScrollPane(textPane);
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Crear un panel para los botones
        JPanel panelBotones = new JPanel();

        // Crear un botón para analizar el código
        JButton botonAnalizar = new JButton("Analizar");
        panelBotones.add(botonAnalizar); // Añadir el botón "Analizar"

        // Crear un botón para generar gráfico
        JButton botongra = new JButton("Generar Gráfico");
        panelBotones.add(botongra); // Añadir el botón "Generar Gráfico"

        // Agregar el panel de botones al panel principal
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        // Crear el panel para mostrar la posición del cursor
        posicionCursor = new JLabel("Fila 1, Columna 1");
        JPanel panelPosicion = new JPanel(new BorderLayout());
        panelPosicion.add(posicionCursor, BorderLayout.EAST);
        panelPrincipal.add(panelPosicion, BorderLayout.NORTH);

        // Actualizar la posición del cursor al moverlo en el JTextPane
        textPane.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                actualizarPosicionCursor();
            }
        });

        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aplicarEstilosEnTiempoReal();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aplicarEstilosEnTiempoReal();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        // Añadir funcionalidad para abrir, guardar y crear archivos
        JMenuItem abrirItem = new JMenuItem("Abrir");
        archivoMenu.add(abrirItem);
        abrirItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirArchivo();
            }
        });

        JMenuItem guardarItem = new JMenuItem("Guardar");
        archivoMenu.add(guardarItem);
        guardarItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarArchivo();
            }
        });

        JMenuItem guardarComoItem = new JMenuItem("Guardar como");
        archivoMenu.add(guardarComoItem);
        guardarComoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarArchivoComo();
            }
        });

        JMenuItem generargarf = new JMenuItem("Generar Gráfico");
        generargarf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generarGrafico();
            }
        });

        JMenuItem Reporteerror = new JMenuItem("Reporte Error Lexico");
        reportesMenu.add(Reporteerror);
        Reporteerror.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (errors != null && !errors.isEmpty()) {  // Asegurarse de que la lista no esté vacía
                    Reporte_Error_Lexico reportes = new Reporte_Error_Lexico(errors);
                    reportes.generarReporte();
                } else {
                    JOptionPane.showMessageDialog(null, "NO AY ERRORES.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JMenuItem VerreporteerrorSintactico = new JMenuItem("Reporte Error Sintactico");
        reportesMenu.add(VerreporteerrorSintactico);
        VerreporteerrorSintactico.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (errorssi != null && !errorssi.isEmpty()) {  // Asegurarse de que la lista no esté vacía
                    Reporte_Error_Sintactico reportes = new Reporte_Error_Sintactico(errorssi);
                    reportes.generarReporte();
                } else {
                    JOptionPane.showMessageDialog(null, "NO AY ERRORES.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JMenuItem RestoRepo = new JMenuItem("Otros Reportes");
        reportesMenu.add(RestoRepo);
        RestoRepo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reporteNumeroOperaciones.mostrarReporte();
                reportesmodi.mostrarReporte();
            }
        });

        // Añadir el panel principal a la ventana
        add(panelPrincipal);

        // Evento para analizar el código
        botonAnalizar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AplicarEstilosAlTexto();
                analizar();
            }
        });

        // Evento para analizar el código
        botongra.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generarGrafico();
            }
        });

        // Permitir escribir desde el principio si no hay archivo abierto
        textPane.setEditable(true); // Se puede escribir incluso sin archivo abierto
    }

    // Método para actualizar la posición del cursor
    private void actualizarPosicionCursor() {
        int pos = textPane.getCaretPosition();
        int fila = 1;
        int columna = 1;

        try {
            fila = textPane.getDocument().getDefaultRootElement().getElementIndex(pos) + 1;
            columna = pos - textPane.getDocument().getDefaultRootElement().getElement(fila - 1).getStartOffset() + 1;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        posicionCursor.setText("Fila " + fila + ", Columna " + columna);
    }

    public void analizar() {
        String texto = textPane.getText();  // Obtener el texto del componente textPane
        Analizador_Lexico analizadorLexico = new Analizador_Lexico(new StringReader(texto));

        try {
            // Limpiar la lista de errores previa
            errors = new ArrayList<>();
            errorssi = new ArrayList<>();

            // Ejecutar el análisis léxico hasta alcanzar el final del archivo
            while (analizadorLexico.yylex() != Analizador_Lexico.YYEOF) {
                // El analizador ya va almacenando los tokens y errores
            }

            // Obtener la lista de tokens reconocidos
            List<String> listaTokens = analizadorLexico.getLista();
            for (String token : listaTokens) {
                System.out.println("Token: " + token);
            }

            // Obtener la lista de errores léxicos y almacenarlos en `errors`
            List<ErrorLexico> listaErrores = analizadorLexico.getListaErrores();
            if (!listaErrores.isEmpty()) {
                errors.addAll(listaErrores);
                System.out.println("Se encontraron errores léxicos:");
                for (ErrorLexico error : listaErrores) {
                    System.out.println("Error: " + error.getDescripcion() + " en línea " + error.getLinea() + ", columna " + error.getColumna());
                }
            }

            // Solo proceder con el análisis sintáctico si no hay errores léxicos
            if (listaErrores.isEmpty()) {
                // Inicializar el analizador sintáctico con los tokens obtenidos
                Analizador_Sintatico analizadorSintactico = new Analizador_Sintatico(listaTokens);

                try {
                    // Ejecutar el análisis sintáctico
                    analizadorSintactico.analizar();
                    // Obtener los errores sintácticos después de realizar el análisis
                    List<ErrorSintatico> listaErroresSintacticos = analizadorSintactico.getListaErrores();
                    reporteNumeroOperaciones = analizadorSintactico.getReporteOperaciones();
                    reportesmodi = analizadorSintactico.getReporteModificacion();
                    // Verificar si se encontraron errores sintácticos y mostrarlos
                    if (!listaErroresSintacticos.isEmpty()) {
                        errorssi.addAll(listaErroresSintacticos);
                        System.out.println("Se encontraron errores sintácticos:");
                        for (ErrorSintatico error : listaErroresSintacticos) {
                            System.out.println("Error: " + error.getDescripcion() + " en línea " + error.getLinea() + ", columna " + error.getColumna());
                        }
                        // Mostrar mensaje con los errores sintácticos
                        JOptionPane.showMessageDialog(null, "Se encontraron errores sintácticos.", "Errores Sintácticos", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Si no hay errores sintácticos, mostrar mensaje de éxito
                        JOptionPane.showMessageDialog(null, "Análisis sintáctico completado sin errores.");
                    }

                } catch (Exception e) {
                    // Si ocurre un error en el análisis sintáctico, mostrar el error
                    JOptionPane.showMessageDialog(null, "Error durante el análisis sintáctico: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Si hay errores léxicos, mostrar un mensaje de error
                JOptionPane.showMessageDialog(null, "No se puede proceder con el análisis sintáctico debido a errores léxicos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para aplicar estilos (resaltado de sintaxis) en el JTextPane
    private void AplicarEstilosAlTexto() {

        String texto = textPane.getText();
        doc.setCharacterAttributes(0, texto.length(), new SimpleAttributeSet(), true);

        // Estilos para tokens
        SimpleAttributeSet estiloPalabrasReservadas = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloPalabrasReservadas, Color.ORANGE);

        SimpleAttributeSet estiloStrings = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloStrings, Color.GREEN);

        SimpleAttributeSet estiloComentarios = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloComentarios, Color.GRAY);

        SimpleAttributeSet estiloDecimales = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloDecimales, Color.BLUE);

        SimpleAttributeSet estiloEnteros = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloEnteros, Color.BLUE);

        SimpleAttributeSet estiloIdentificadores = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloIdentificadores, Color.CYAN);

        SimpleAttributeSet estiloBooleanos = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloBooleanos, Color.BLUE);

        SimpleAttributeSet estiloFuncionesAgregacion = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloFuncionesAgregacion, Color.BLUE);

        SimpleAttributeSet estiloOperadores = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloOperadores, Color.BLACK);

        SimpleAttributeSet estiloTiposDatos = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloTiposDatos, Color.MAGENTA);

        SimpleAttributeSet estiloTiposDatosLogios = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloTiposDatosLogios, Color.ORANGE);

        SimpleAttributeSet estiloComentarioLinea = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloComentarioLinea, Color.GRAY);

        SimpleAttributeSet estiloSignos = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloSignos, Color.BLACK);

        SimpleAttributeSet estiloFechas = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloFechas, Color.YELLOW);

        // Limpiar estilos previos
        doc.setCharacterAttributes(0, texto.length(), new SimpleAttributeSet(), true);

        // Palabras reservadas
        String[] palabrasReservadas = {"CREATE", "DATABASE", "TABLE", "KEY", "NULL", "PRIMARY", "UNIQUE", "FOREIGN", "REFERENCES",
            "ALTER", "ADD", "COLUMN", "TYPE", "DROP", "CONSTRAINT", "IF", "EXIST","EXISTS","CASCADE", "ON", "DELETE", "SET", "UPDATE", "INSERT",
            "INTO", "VALUES", "SELECT", "FROM", "WHERE", "AS", "GROUP", "ORDER", "BY", "ASC", "DESC", "LIMIT", "JOIN"};

        for (String palabra : palabrasReservadas) {
            int index = texto.indexOf(palabra);
            while (index >= 0) {
                doc.setCharacterAttributes(index, palabra.length(), estiloPalabrasReservadas, false);
                index = texto.indexOf(palabra, index + 1);
            }
        }

        // Estilo para fechas
        String fechaRegex = "'\\d{4}-\\d{2}-\\d{2}'";
        Matcher matcherFecha = Pattern.compile(fechaRegex).matcher(texto);
        while (matcherFecha.find()) {
            doc.setCharacterAttributes(matcherFecha.start(), matcherFecha.end() - matcherFecha.start(), estiloFechas, false);
        }

        // Estilo para cadenas de texto (excluyendo fechas)
        int startIndex = 0;
        while ((startIndex = texto.indexOf("'", startIndex)) != -1) {
            int endIndex = texto.indexOf("'", startIndex + 1);
            if (endIndex != -1) {
                String cadenaPosible = texto.substring(startIndex, endIndex + 1);

                // Verifica si la cadena es una fecha
                if (!cadenaPosible.matches(fechaRegex)) {
                    // Si no es una fecha, aplica estilo de cadenas
                    doc.setCharacterAttributes(startIndex, endIndex - startIndex + 1, estiloStrings, false);
                }
                startIndex = endIndex + 1;
            } else {
                break;
            }
        }

        // Comentarios
        if (texto.contains("--")) {
            int comentarioIndex = texto.indexOf("--");
            doc.setCharacterAttributes(comentarioIndex, texto.length() - comentarioIndex, estiloComentarioLinea, false);
        }

        // Números decimales
        String decimalRegex = "\\b\\d+\\.\\d+\\b|\\b\\d+\\.\\d*\\b";
        Matcher matcher = Pattern.compile(decimalRegex).matcher(texto);
        while (matcher.find()) {
            doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), estiloDecimales, false);
        }

        // Números enteros
        String enteroRegex = "\\b\\d+\\b";
        matcher = Pattern.compile(enteroRegex).matcher(texto);
        while (matcher.find()) {
            doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), estiloEnteros, false);
        }
        
        // Estilo para identificadores en snake_case
        String identificadorSnakeCaseRegex = "\\b[a-z_][a-z0-9_]*\\b";
        matcher = Pattern.compile(identificadorSnakeCaseRegex).matcher(texto);
        while (matcher.find()) {
            String identificador = matcher.group();

            // Verifica si no es una palabra reservada
            if (!Arrays.asList(palabrasReservadas).contains(identificador)) {
                // Aplica el estilo a los identificadores válidos
                doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), estiloIdentificadores, false);
            }
        }
        // Booleanos
        String[] booleanos = {"TRUE", "FALSE"};
        for (String booleano : booleanos) {
            int booleanIndex = texto.indexOf(booleano);
            while (booleanIndex >= 0) {
                doc.setCharacterAttributes(booleanIndex, booleano.length(), estiloBooleanos, false);
                booleanIndex = texto.indexOf(booleano, booleanIndex + 1);
            }
        }

        // Funciones de Agregación
        String[] funcionesAgregacion = {"SUM", "AVG", "COUNT", "MAX", "MIN"};
        for (String funcion : funcionesAgregacion) {
            int funcionIndex = texto.indexOf(funcion);
            while (funcionIndex >= 0) {
                doc.setCharacterAttributes(funcionIndex, funcion.length(), estiloFuncionesAgregacion, false);
                funcionIndex = texto.indexOf(funcion, funcionIndex + 1);
            }
        }

        // Operadores (Aritméticos y Relacionales)
        String operadoresRegex = "[+\\-*/<>=]";
        matcher = Pattern.compile(operadoresRegex).matcher(texto);
        while (matcher.find()) {
            doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), estiloOperadores, false);
        }

        // Signos de puntuación
        String signosRegex = "[(),;.=]";
        matcher = Pattern.compile(signosRegex).matcher(texto);
        while (matcher.find()) {
            doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), estiloSignos, false);
        }

        // Tipos de datos
        String[] tiposDatos = {"INTEGER", "BIGINT", "VARCHAR", "DECIMAL", "DATE", "TEXT", "BOOLEAN", "SERIAL", "NUMERIC"};
        for (String tipo : tiposDatos) {
            int tipoIndex = texto.indexOf(tipo);
            while (tipoIndex >= 0) {
                doc.setCharacterAttributes(tipoIndex, tipo.length(), estiloTiposDatos, false);
                tipoIndex = texto.indexOf(tipo, tipoIndex + 1);
            }
        }

        // Tipos de datos
        String[] tiposDatosLogicos = {"AND", "OR", "NOT"};
        for (String tipo : tiposDatosLogicos) {
            int tipoIndex = texto.indexOf(tipo);
            while (tipoIndex >= 0) {
                doc.setCharacterAttributes(tipoIndex, tipo.length(), estiloTiposDatosLogios, false);
                tipoIndex = texto.indexOf(tipo, tipoIndex + 1);
            }
        }

    }

    // Método para abrir archivos
    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                textPane.read(reader, null);
                archivoAbierto = true; // Ahora tenemos un archivo abierto
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Método para guardar archivos
    private void guardarArchivo() {
        if (!archivoAbierto) {
            guardarArchivoComo();
        } else {
            // Si el archivo está abierto, guardarlo directamente en la misma ubicación
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("archivo_guardado.txt"));
                writer.write(textPane.getText());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para guardar archivos con "Guardar como"
    private void guardarArchivoComo() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showSaveDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
                textPane.write(writer);
                archivoAbierto = true; // Se guarda el archivo como nuevo y se marca como abierto
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void generarGrafico() {
        // Obtener la lista de tablas y modificadores desde el analizador
        List<Tabla> tablas = Analizador_Sintatico.getTablas();
        List<Modificador> modificadores = Analizador_Sintatico.getModificadores();

        // Verifica si hay tablas y/o modificadores disponibles
        if (tablas.isEmpty() && modificadores.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay tablas ni modificadores para graficar.");
            return; // No hacer nada si no hay tablas ni modificadores
        }

        // Crea la instancia de Grafica para las tablas y modificadores disponibles
        Grafica grafica = new Grafica(tablas, modificadores);
        // Muestra la información (esto generará el gráfico y lo mostrará)
        grafica.mostrarInformacion();
    }

    // Método para aplicar estilos en tiempo real
    private void aplicarEstilosEnTiempoReal() {
        SwingUtilities.invokeLater(() -> AplicarEstilosAlTexto());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Proyecto_Final_202LF_202230094().setVisible(true);
        });
    }
}
