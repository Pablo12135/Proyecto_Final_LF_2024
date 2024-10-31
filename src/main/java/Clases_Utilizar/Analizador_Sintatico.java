package Clases_Utilizar;

import java.util.List;
import java.util.Stack;
import Reportes.ErrorSintatico;
import Reportes.ReporteNumeroOperaciones;
import Reportes.ReporteTablasModificadas;
import java.util.ArrayList;

public class Analizador_Sintatico {

    private List<String> tokens; // Lista de tokens obtenidos del analizador léxico
    private int currentTokenIndex; // Índice del token actual
    private String tokenActual;
    private Stack<String> pila; // Pila explícita para el autómata
    private List<ErrorSintatico> erroresSintactico = new ArrayList<>();
    private static List<Tabla> tablas = new ArrayList<>();
    private static List<Modificador> modificadores = new ArrayList<>();
    private String nombreTablaM;
    private ReporteNumeroOperaciones reporteOperaciones = new ReporteNumeroOperaciones();
    private ReporteTablasModificadas reporteOperacionesModificacion = new ReporteTablasModificadas();

    public Analizador_Sintatico(List<String> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.tokenActual = tokens.isEmpty() ? null : tokens.get(0); // Inicializa con el primer token
        this.pila = new Stack<>(); // Inicializa la pila
    }

    // Avanza al siguiente token
    private void siguienteToken() {
        currentTokenIndex++;
        if (currentTokenIndex < tokens.size()) {
            tokenActual = tokens.get(currentTokenIndex);
        } else {
            tokenActual = null; // No hay más tokens
        }
    }

    // Verifica si el token actual coincide con el esperado y avanza
    private boolean coincidir(String tokenEsperado) {
        if (tokenActual != null && tokenActual.equals(tokenEsperado)) {
            siguienteToken();
            return true;
        } else {
            return false; // Error de coincidencia
        }
    }

    // Método para verificar si un token es un identificador
    private boolean esIdentificador(String token) {
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }

    // Verificar si el token es un dato primitivo válido
    private boolean esDatoPrimitivo(String token) {
        return esEntero(token) || esDecimal(token) || esCadena(token) || esBooleano(token) || esFecha(token);
    }

    // Verificar si el token es un número entero
    private boolean esEntero(String token) {
        return token.matches("\\d+");
    }

    // Verificar si el token es un número decimal
    private boolean esDecimal(String token) {
        return token.matches("\\d+\\.\\d+");
    }

    // Verificar si el token es una cadena
    private boolean esCadena(String token) {
        return token.startsWith("'") && token.endsWith("'");
    }

    // Verificar si el token es un booleano
    private boolean esBooleano(String token) {
        return token.equals("TRUE") || token.equals("FALSE");
    }

    // Verificar si el token es una fecha (en formato 'YYYY-MM-DD')
    private boolean esFecha(String token) {
        return token.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    // Método para verificar si un token es un tipo de dato válido
    private boolean esTipoDeDato(String token) {
        return token.equals("SERIAL") || token.equals("INTEGER") || token.equals("BIGINT") || token.equals("VARCHAR")
                || token.equals("DECIMAL") || token.equals("DATE") || token.equals("TEXT") || token.equals("BOOLEAN")
                || token.matches("NUMERIC") || token.matches("DECIMAL\\(\\d+,\\d+\\)");
    }

    // Método principal para el análisis de la sentencia SQL
    public void analizar() {
        if (tokenActual == null) {
            throw new RuntimeException("Error: No hay tokens para analizar.");
        }

        // Iniciar con el símbolo inicial de la gramática
        pila.push("SENTENCIA_SQL"); // Estado inicial
        System.out.println("Iniciando análisis sintáctico...");

        while (!pila.isEmpty()) {
            String simbolo = pila.pop(); // Obtener el símbolo superior de la pila
            System.out.println("Procesando símbolo: " + simbolo);
            System.out.println("Estado actual de la pila: " + pila);

            switch (simbolo) {
                case "SENTENCIA_SQL":
                    if (tokenActual.equals("CREATE")) {
                        pila.push("TIPO_CREATE"); // Verificamos el tipo de CREATE
                        reporteOperaciones.contarOperacion("CREATE"); // Contar la operación CREATE
                    } else if (tokenActual.equals("ALTER")) {
                        reporteOperaciones.contarOperacion("ALTER"); // Contar la operación ALTER
                        procesarAlterTable();
                    } else if (tokenActual.equals("TABLE")) {
                        procesarCreateTable();
                    } else if (tokenActual.equals("DROP")) {
                        procesarDropTable();
                    } else if (tokenActual.equals("UPDATE")) {
                        reporteOperaciones.contarOperacion("UPDATE"); // Contar la operación UPDATE
                        procesarUpdate();
                    } else if (tokenActual.equals("DELETE")) {
                        reporteOperaciones.contarOperacion("DELETE"); // Contar la operación DELETE
                        procesarDelete();
                    } else if (tokenActual.equals("SELECT")) {
                        reporteOperaciones.contarOperacion("SELECT"); // Contar la operación SELECT
                        procesarSelect();
                    } else if (tokenActual.equals("INSERT")) {
                        procesarInsertInto();
                    } else {
                        throw new RuntimeException("Error: Se esperaba una sentencia SQL válida.");
                    }
                    break;

                case "TIPO_CREATE":
                    System.out.println("Token actual después de CREATE: " + tokenActual);

                    // Avanzar al siguiente token
                    obtenerSiguienteToken();  // Avanzamos el puntero del token actual

                    // Ahora que hemos avanzado, identificamos si es DATABASE o TABLE
                    if (tokenActual.equals("DATABASE")) {
                        System.out.println("Se ha detectado DATABASE");
                        procesarCreateDatabase();
                    } else if (tokenActual.equals("TABLE")) {
                        System.out.println("Se ha detectado TABLE");
                        procesarCreateTable();
                    } else {
                        // throw new RuntimeException("Error: Se esperaba 'DATABASE' o 'TABLE' después de 'CREATE'. Token actual: " + tokenActual);
                        agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba 'DATABASE' "
                                + "o 'TABLE' después de 'CREATE'");
                    }
                    break;
                case "ASIGNACION":
                    procesarAsignacion();
                    break;

                case "WHERE":
                    procesarWhere();
                    break;

                case "GROUP_BY":
                    procesarGroupBy();
                    break;

                case "ORDER_BY":
                    procesarOrderBy();
                    break;

                case "LIMIT":
                    procesarLimit();
                    break;

            }
        }
    }

    // Procesar la sentencia CREATE TABLE
    private void procesarCreateTable() {
        // Coincidir la palabra clave TABLE
        if (!coincidir("TABLE")) {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba 'TABLE' después de 'CREATE'.");
            return;
        }
        siguienteToken();
        // Verificar si el siguiente token es un identificador (nombre de la tabla)
        if (esIdentificador(tokenActual)) {
            String nombreTabla = tokenActual;
            coincidir(tokenActual); // Nombre de la tabla
            Tabla nuevaTabla = new Tabla(nombreTabla);
            // Coincidir el paréntesis de apertura
            if (!coincidir("(")) {
                agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba '(' después del nombre de la tabla.");
                return;
            }

            procesarEstructuraDeDeclaracion(nuevaTabla); // Llama a procesarEstructuraDeDeclaracion para manejar columnas y restricciones

            if (!coincidir(")")) { // Cierre de paréntesis de la declaración de columnas
                agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba ')'.");
                return;
            }

            if (!coincidir(";")) {
                System.out.println("Error: " + tokenActual);
                agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un punto y coma (;) al final de la sentencia CREATE TABLE, pero no se encontró.");
            } else {
                tablas.add(nuevaTabla); // Agregar la tabla solo si todo fue correcto
            }

        } else {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador válido para el nombre de la tabla.");
        }
    }

    private void procesarCreateDatabase() {
        coincidir("CREATE");
        coincidir("DATABASE");

        if (esIdentificador(tokenActual)) {
            coincidir(tokenActual); // Nombre de la base de datos
        } else {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador válido para el nombre de la base de datos");
            return;
        }
        siguienteToken();
        // Verificamos si el token actual es null, lo que indica que no hay más tokens
        if (tokenActual == null) {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un punto y coma (;) al final de la sentencia CREATE DATABASE, pero no se encontró.");
            return;
        }
    }

    // Procesar la sentencia ALTER TABLE
    private void procesarAlterTable() {
        coincidir("ALTER");
        coincidir("TABLE");
        System.out.println("Token actual antes de verificar el nombre de la tabla: " + tokenActual); // Depuración

        // Verificar si el siguiente token es un identificador (nombre de la tabla)
        if (esIdentificador(tokenActual)) {
            nombreTablaM = tokenActual;
            coincidir(tokenActual); // Coincidir con el nombre de la tabla y avanzar al siguiente token
            System.out.println("Nombre de la tabla detectado: " + nombreTablaM); // Depuración
        } else {
            // Si no hay un identificador válido, se detiene y se genera un error
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(),
                    "Se esperaba el nombre de la tabla después de 'ALTER TABLE'");
            return; // Detener el análisis si falta el nombre de la tabla
        }

        // Continuar con el tipo de modificación (ADD, ALTER, DROP)
        String tipoModificacion = tokenActual;

        // Verificar y procesar el tipo de modificación
        if (tipoModificacion.equals("ADD")) {
            reporteOperacionesModificacion.agregarModificacion(nombreTablaM, "ADD COLUMN", 1, 1);
            coincidir("ADD"); // Coincidir con "ADD"
            procesarAlterAdd(); // Procesar la modificación ADD
        } else if (tipoModificacion.equals("ALTER")) {
            reporteOperacionesModificacion.agregarModificacion(nombreTablaM, "ALTER COLUMN", 1, 1);
            coincidir("ALTER"); // Coincidir con "ALTER"
            procesarAlterColumn(); // Procesar la modificación ALTER
        } else if (tipoModificacion.equals("DROP")) {
            reporteOperacionesModificacion.agregarModificacion(nombreTablaM, "DROP COLUMN", 1, 1);
            coincidir("DROP"); // Coincidir con "DROP"
            procesarAlterDrop(); // Procesar la modificación DROP
        } else {
            // Si no se reconoce el tipo de modificación, generar un error
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(),
                    "Se esperaba una operación válida después de 'ALTER TABLE' (ADD, ALTER, DROP)");
            return; // Detener el análisis si hay un error en el tipo de modificación
        }

        // Finalmente, verificar que la sentencia termine con un punto y coma
        if (!coincidir(";")) {// Verificar que termine con ";"
            System.out.println("errro " + tokenActual);
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(),
                    "Se esperaba una (;) al final de la sentencia ");
        }
    }

    // Procesar las opciones ADD dentro de ALTER TABLE
    private void procesarAlterAdd() {
        coincidir("ADD");

        // Verificar si se está añadiendo una nueva columna
        if (tokenActual.equals("COLUMN")) {
            coincidir("COLUMN");
            if (esIdentificador(tokenActual)) {
                String nombreColumna = tokenActual; // Nombre de la columna
                coincidir(tokenActual); // Coincidir con el nombre de la columna

                if (esTipoDeDato(tokenActual)) {
                    String tipoDato = tokenActual; // Tipo de dato
                    coincidir(tokenActual); // Coincidir con el tipo de dato
                    System.out.println("Se agregó la columna " + nombreColumna + " de tipo " + tipoDato);
                    modificadores.add(new Modificador("ADD COLUMN ", nombreTablaM, nombreColumna, tipoDato)); // Registrar la modificación
                } else {
                    agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un tipo de dato válido");
                    return;
                }
            } else {
                agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador válido para la columna");
                return;
            }
        } // Verificar si se está añadiendo una restricción
        else if (tokenActual.equals("CONSTRAINT")) {
            reporteOperacionesModificacion.agregarModificacion(nombreTablaM, "ADD CONSTRAINT", 1, 1);
            coincidir("CONSTRAINT");
            if (esIdentificador(tokenActual)) {
                String nombreRestriccion = tokenActual; // Nombre de la restricción
                coincidir(tokenActual); // Coincidir con el nombre de la restricción

                // Procesar restricciones UNIQUE
                if (tokenActual.equals("UNIQUE")) {
                    coincidir("UNIQUE");
                    coincidir("(");
                    if (esIdentificador(tokenActual)) {
                        String columnaUnica = tokenActual; // Columna única
                        coincidir(tokenActual); // Coincidir con el nombre de la columna única
                        coincidir(")");
                        System.out.println("Se agregó la restricción UNIQUE en la columna " + columnaUnica);
                        // Registrar la modificación aquí
                    } else {
                        agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador válido para la columna única");
                        return;
                    }
                } // Procesar restricciones FOREIGN KEY
                else if (tokenActual.equals("FOREIGN")) {
                    coincidir("FOREIGN");
                    coincidir("KEY");
                    coincidir("(");
                    if (esIdentificador(tokenActual)) {
                        String columnaClaveForanea = tokenActual; // Columna de clave foránea
                        coincidir(tokenActual); // Coincidir con el nombre de la columna
                        coincidir(")");

                        // Procesar referencia
                        coincidir("REFERENCES");
                        if (esIdentificador(tokenActual)) {
                            String tablaReferenciada = tokenActual; // Tabla referenciada
                            coincidir(tokenActual); // Coincidir con el nombre de la tabla referenciada
                            coincidir("(");
                            if (esIdentificador(tokenActual)) {
                                String columnaReferenciada = tokenActual; // Columna referenciada
                                coincidir(tokenActual); // Coincidir con el nombre de la columna referenciada
                                coincidir(")");

                                // Manejar ON DELETE
                                if (tokenActual.equals("ON")) {
                                    coincidir("ON");
                                    if (tokenActual.equals("DELETE")) {
                                        coincidir("DELETE");
                                        if (tokenActual.equals("SET")) {
                                            coincidir("SET");
                                            if (tokenActual.equals("NULL")) {
                                                coincidir("NULL");
                                            } else {
                                                agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba 'NULL' después de 'SET'");
                                                return;
                                            }
                                        } else {
                                            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba 'SET' después de 'ON DELETE'");
                                            return;
                                        }
                                    } else {
                                        agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba 'DELETE' después de 'ON'");
                                        return;
                                    }
                                }

                                // Manejar ON UPDATE
                                if (tokenActual.equals("ON")) {
                                    coincidir("ON");
                                    if (tokenActual.equals("UPDATE")) {
                                        coincidir("UPDATE");
                                        if (tokenActual.equals("CASCADE")) {
                                            coincidir("CASCADE");
                                        } else {
                                            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba 'CASCADE' después de 'ON UPDATE'");
                                            return;
                                        }
                                    } else {
                                        agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba 'UPDATE' después de 'ON'");
                                        return;
                                    }
                                }

                                System.out.println("Se agregó la restricción FOREIGN KEY en la columna " + columnaClaveForanea
                                        + " referenciando " + tablaReferenciada + "(" + columnaReferenciada + ") "
                                        + "ON DELETE SET NULL ON UPDATE CASCADE");
                            } else {
                                agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador para la columna referenciada");
                                return;
                            }
                        } else {
                            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador para la tabla referenciada");
                            return;
                        }
                    } else {
                        agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador para la columna clave foránea");
                        return;
                    }
                } else {
                    agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba una restricción válida (UNIQUE, FOREIGN KEY)");
                }
            } else {
                agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador válido para la restricción");
                return;
            }
        } else {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba 'COLUMN' o 'CONSTRAINT' después de 'ADD'");
        }
    }

    // Procesar las opciones ALTER COLUMN dentro de ALTER TABLE
    private void procesarAlterColumn() {
        coincidir("ALTER");
        coincidir("COLUMN");

        // Verificar si el siguiente token es un identificador (nombre de la columna)
        if (esIdentificador(tokenActual)) {
            String nombreColumna = tokenActual;
            coincidir(tokenActual); // Coincidir con el nombre de la columna
            System.out.println("Nombre de la columna a alterar: " + nombreColumna);

            // Verificar si el siguiente token es 'TYPE'
            if (tokenActual.equals("TYPE")) {
                coincidir("TYPE");

                // Verificar si el siguiente token es un tipo de dato válido
                if (esTipoDeDato(tokenActual)) {
                    String nuevoTipoDato = tokenActual;
                    coincidir(tokenActual); // Coincidir con el nuevo tipo de dato
                    System.out.println("Nuevo tipo de dato para la columna: " + nuevoTipoDato);
                    modificadores.add(new Modificador("ALTER COLUMN ", nombreTablaM, nombreColumna, nuevoTipoDato)); // Registrar la modificación
                    // Verificar si el tipo de dato acepta parámetros (ej: NUMERIC(12, 2), CHAR(50), etc.)
                    if (tokenActual.equals("(")) {
                        coincidir("("); // Coincidir con el paréntesis de apertura
                        String primerParametro = tokenActual;
                        coincidir(tokenActual); // Coincidir con el primer número (precisión o longitud)

                        // Verificar si es un tipo con dos parámetros (ej: NUMERIC(12, 2))
                        if (tokenActual.equals(",")) {
                            coincidir(","); // Coincidir con la coma
                            String segundoParametro = tokenActual;
                            coincidir(tokenActual); // Coincidir con el segundo número (escala)
                            coincidir(")"); // Coincidir con el paréntesis de cierre
                            System.out.println("Tipo de dato con dos parámetros: " + nuevoTipoDato + "(" + primerParametro + ", " + segundoParametro + ")");
                        } else {
                            // Tipo de dato con un solo parámetro (ej: CHAR(50))
                            coincidir(")"); // Coincidir con el paréntesis de cierre
                            System.out.println("Tipo de dato con un parámetro: " + nuevoTipoDato + "(" + primerParametro + ")");
                        }
                    }

                } else {
                    agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(),
                            "Se esperaba un tipo de dato válido después de 'TYPE'");
                    return;
                }

            } else {
                agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(),
                        "Se esperaba 'TYPE' después de 'ALTER COLUMN'");
                return;
            }

        } else {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(),
                    "Se esperaba un identificador válido para el nombre de la columna después de 'ALTER COLUMN'");
            return;
        }
    }

    // Procesar las opciones DROP COLUMN dentro de ALTER TABLE
    private void procesarAlterDrop() {
        coincidir("DROP");
        if (!tokenActual.equals("COLUMN")) {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "despues de ADD se necesita COLUMN ");
        }
        if (tokenActual.equals("COLUMN")) {
            coincidir("COLUMN");
            if (esIdentificador(tokenActual)) {
                coincidir(tokenActual); // Nombre de la columna
                System.out.println("perrro " + tokenActual);
            } else {
                agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador válido para la columna");
                return;
            }
        }
    }

    // Procesar la sentencia DROP TABLE
    private void procesarDropTable() {
        coincidir("DROP");
        coincidir("TABLE");

        if (tokenActual.equals("IF")) {
            coincidir("IF");
            coincidir("EXISTS");
        }

        if (esIdentificador(tokenActual)) {
            nombreTablaM = tokenActual;
            coincidir(tokenActual); // Nombre de la tabla
            modificadores.add(new Modificador("DROP TABLE ", nombreTablaM, tokenActual, "")); //
        } else {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba el nombre de la tabla en la sentencia DROP TABLE");
            return;
        }

        if (tokenActual.equals("CASCADE")) {
            coincidir("CASCADE");
        }

        if (!coincidir(";")) {
            System.out.println("esta llefando aca");
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba ';' al final de la sentencia DROP TABLE");
        }
        reporteOperacionesModificacion.agregarModificacion(nombreTablaM, "DROP TABLE", 1, 1);
    }

    // Procesar las declaraciones de columnas en CREATE TABLE
    private void procesarEstructuraDeDeclaracion(Tabla tabla) {
        while (tokenActual != null && !tokenActual.equals(")")) {
            // Verificar si es una declaración de CONSTRAINT o FOREIGN KEY
            if (tokenActual.equals("CONSTRAINT") || tokenActual.equals("FOREIGN")) {
                procesarLlaveForanea(tabla); // Llamar al método que procesa la llave foránea
                if (tokenActual.equals(",")) {
                    coincidir(","); // Avanzar si hay una coma después de la constraint
                }
                continue;
            }

            // Procesar una columna regular
            if (esIdentificador(tokenActual)) {
                String nombreColumna = tokenActual;
                coincidir(tokenActual); // Nombre de la columna

                // Procesar el tipo de dato de la columna
                String tipoDato = tokenActual;
                if (esTipoDeDato(tokenActual)) {
                    coincidir(tokenActual); // Tipo de dato
                    // Procesar parámetros del tipo de dato, si existen (como VARCHAR(50) o DECIMAL(10,2))
                    if (tokenActual.equals("(")) {
                        tipoDato += procesarParametrosTipoDato();
                    }
                } else {
                    agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un tipo de dato válido.");
                    return;  // Detener el procesamiento si falta el tipo de dato
                }

                // Procesar restricciones de la columna, como PRIMARY KEY, UNIQUE, NOT NULL
                boolean esLlavePrimaria = false;
                boolean esUnique = false;
                boolean esNotNull = false;

                while (tokenActual.equals("PRIMARY") || tokenActual.equals("UNIQUE") || tokenActual.equals("NOT")) {
                    if (tokenActual.equals("PRIMARY")) {
                        coincidir("PRIMARY");
                        coincidir("KEY");
                        esLlavePrimaria = true;
                    } else if (tokenActual.equals("UNIQUE")) {
                        coincidir("UNIQUE");
                        esUnique = true;
                    } else if (tokenActual.equals("NOT")) {
                        coincidir("NOT");
                        coincidir("NULL");
                        esNotNull = true;
                    }
                }

                // Crear la columna y agregarla a la tabla
                Columna columna = new Columna(nombreColumna, tipoDato, esLlavePrimaria);
                columna.setUnique(esUnique);
                columna.setNotNull(esNotNull);
                tabla.agregarColumna(columna);

                // Avanzar si hay una coma, continuar con la siguiente columna
                if (tokenActual.equals(",")) {
                    coincidir(",");
                } else if (!tokenActual.equals(")")) {
                    agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba ',' o ')' en la definición de la columna.");
                    return;
                }
            } else {
                agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador para el nombre de la columna.");
                return;
            }
        }
    }

    // Procesar los parámetros de tipo de dato (como VARCHAR(50) o DECIMAL(10,2))
    private String procesarParametrosTipoDato() {
        StringBuilder parametros = new StringBuilder();
        coincidir("(");
        while (!tokenActual.equals(")")) {
            parametros.append(tokenActual);
            coincidir(tokenActual); // Agregar el valor del parámetro
            if (tokenActual.equals(",")) {
                parametros.append(",");
                coincidir(","); // Separador de parámetros
            }
        }
        coincidir(")"); // Cerrar los parámetros
        return parametros.toString();
    }
// Procesar la estructura de llaves foráneas (FOREIGN KEY)

    private void procesarLlaveForanea(Tabla tabla) {

        coincidir("CONSTRAINT");
        String constraintName = tokenActual; // Nombre de la constraint
        coincidir(tokenActual);

        coincidir("FOREIGN");
        coincidir("KEY");
        coincidir("(");
        if (esIdentificador(tokenActual)) {
            coincidir(tokenActual); // Nombre de la columna de la llave foránea
        }
        coincidir(")");
        coincidir("REFERENCES");
        if (esIdentificador(tokenActual)) {
            coincidir(tokenActual); // Nombre de la tabla referenciada
            coincidir("(");
            if (esIdentificador(tokenActual)) {
                coincidir(tokenActual);
            }
            coincidir(")");
        }
    }

    // Procesar la sentencia INSERT INTO
    private void procesarInsertInto() {
        coincidir("INSERT");
        coincidir("INTO");

        if (esIdentificador(tokenActual)) {
            coincidir(tokenActual); // Nombre de la tabla
        } else {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador para la tabla en la sentencia INSERT INTO");
            return;
        }

        coincidir("(");
        procesarListaIdentificadores();
        coincidir(")");

        if (!coincidir("VALUES")) {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba VALUES");
        }

        procesarListaDeValores();

        while (tokenActual != null && tokenActual.equals(",")) {
            coincidir(",");
            procesarListaDeValores();
        }

        if (!coincidir(";")) {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un punto y coma (;) al final de la sentencia INSERT INTO");
            return;
        }
    }

    // Procesar lista de identificadores de columnas
    private void procesarListaIdentificadores() {
        if (esIdentificador(tokenActual)) {
            coincidir(tokenActual); // Primer identificador de columna
            while (tokenActual != null && tokenActual.equals(",")) {
                coincidir(",");
                coincidir(tokenActual); // Siguiente identificador
            }
        }
    }

    // Procesar lista de valores para INSERT INTO
    private void procesarListaDeValores() {
        coincidir("(");
        procesarDato(); // Primer dato o expresión
        while (tokenActual != null && tokenActual.equals(",")) {
            coincidir(",");
            procesarDato(); // Siguiente dato o expresión
        }
        coincidir(")");
    }

    // Procesar un dato o una expresión
    private void procesarDato() {
        if (esDatoPrimitivo(tokenActual)) {
            coincidir(tokenActual); // Coincide con un tipo de dato primitivo
        } else if (tokenActual.equals("(")) {
            // Si es una expresión entre paréntesis, la procesamos
            coincidir("(");
            procesarExpresion();
            coincidir(")");
        } else {
            procesarExpresion(); // Si es una expresión compleja
        }
    }

    // Procesar una expresión aritmética, racional o lógica
    private void procesarExpresion() {
        procesarTermino();
        while (tokenActual != null && (tokenActual.equals("+") || tokenActual.equals("-")
                || tokenActual.equals("*") || tokenActual.equals("/") || tokenActual.equals("<")
                || tokenActual.equals(">") || tokenActual.equals("OR") || tokenActual.equals("AND"))) {
            coincidir(tokenActual); // Operador
            procesarTermino();
        }
    }

    // Procesar un término en una expresión
    private void procesarTermino() {
        if (esDatoPrimitivo(tokenActual)) {
            coincidir(tokenActual); // Coincide con un dato
        } else if (tokenActual.equals("(")) {
            coincidir("(");
            procesarExpresion();
            coincidir(")");
        }
    }

    // Procesar una asignación (columna = valor)
    private void procesarAsignacion() {
        if (esDatoPrimitivo(tokenActual)) {
            coincidir(tokenActual); // Coincide con un dato
        } else if (tokenActual.equals("(")) {
            coincidir("(");
            procesarExpresion();
            coincidir(")");
        } else {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un dato primitivo o una expresión entre paréntesis");
            return;
        }
    }

    // Procesar la sentencia UPDATE
    private void procesarUpdate() {
        coincidir("UPDATE"); // Coincide con la palabra clave UPDATE

        if (esIdentificador(tokenActual)) {
            coincidir(tokenActual); // Nombre de la tabla o vista
        } else {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador para la tabla.");
        }

        coincidir("SET"); // Coincide con la palabra clave SET

        procesarListaAsignaciones(); // Procesa las asignaciones (columna = valor)

        // Verificar si hay cláusula WHERE opcional
        if (tokenActual != null && tokenActual.equals("WHERE")) {
            coincidir("WHERE");
            procesarExpresion(); // Procesar la expresión de la cláusula WHERE
        } else if (tokenActual != null && !tokenActual.equals(";")) {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba 'WHERE' o ';'.");
        }

        coincidir(";"); // Fin de la sentencia
    }

    // Procesar la lista de asignaciones en SET (columna = valor)
    private void procesarListaAsignaciones() {
        procesarAsignacion(); // Procesa la primera asignación

        while (tokenActual != null && tokenActual.equals(",")) {
            coincidir(","); // Si hay una coma, procesamos la siguiente asignación
            procesarAsignacion();
        }
    }

    // Procesar la cláusula WHERE (opcional)
    private void procesarWhere() {
        coincidir("WHERE");

        // Procesar la primera condición o el bloque entre paréntesis
        procesarCondicionOParentesis();

        // Mientras sigamos encontrando operadores lógicos, procesamos más condiciones
        while (tokenActual != null && (tokenActual.equals("AND") || tokenActual.equals("OR"))) {
            coincidir(tokenActual);  // Coincidir con AND o OR
            procesarCondicionOParentesis();  // Procesar la siguiente condición o bloque
        }

    }

    // Procesar una condición individual o un bloque entre paréntesis
    private void procesarCondicionOParentesis() {
        if (tokenActual.equals("(")) {
            coincidir("(");  // Coincidir con el paréntesis de apertura
            procesarCondicion();  // Procesar la condición dentro del paréntesis
            coincidir(")");  // Coincidir con el paréntesis de cierre
        } else {
            procesarCondicion();  // Procesar la condición fuera de paréntesis
        }
    }

// Procesar una condición individual
    private void procesarCondicion() {
        // Verificar si el token actual es un identificador (columna)
        if (esIdentificador(tokenActual)) {
            coincidir(tokenActual); // Columna

            // Verificar operador de comparación
            if (tokenActual.equals("=") || tokenActual.equals(">") || tokenActual.equals("<")
                    || tokenActual.equals(">=") || tokenActual.equals("<=")) {
                coincidir(tokenActual); // Operador de comparación
            } else {
                // Error: Se esperaba un operador de comparación válido en la cláusula WHERE
                agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un operador de comparación válido en la cláusula WHERE");
            }

            // Verificar valor válido
            if (esTipoDeDato(tokenActual) || tokenActual.matches("[0-9]+") || tokenActual.matches("'[a-zA-Z0-9 ]*'")) {
                coincidir(tokenActual); // Valor
            } else {
                // Error: Se esperaba un valor válido en la cláusula WHERE
                agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un valor válido en la cláusula WHERE");
            }
        } else {
            // Error: Se esperaba un identificador válido en la cláusula WHERE
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador válido en la cláusula WHERE");
        }
    }

    // Asumiendo que tienes un método para manejar las palabras reservadas y los identificadores.
    public void analizarEliminar(String linea) {
        String[] tokens = linea.split("\\s+"); // Divide la línea en partes por espacios.

        // Verificamos la palabra clave 'DELETE'
        if (!tokens[0].equalsIgnoreCase("DELETE")) {
            throw new Error("Se esperaba 'DELETE'.");
        }

        // Verificamos la palabra clave 'FROM'
        if (!tokens[1].equalsIgnoreCase("FROM")) {
            throw new Error("Se esperaba 'FROM'.");
        }

        // Verificamos el identificador de la tabla
        String identificador = tokens[2];
        if (!esIdentificadorValido(identificador)) {
            throw new Error("El identificador de la tabla no es válido.");
        }

        // Verificamos si hay un 'WHERE' opcional
        if (tokens.length == 4) {
            if (!tokens[3].equalsIgnoreCase("WHERE")) {
                //    throw new Error("Si se incluye 'WHERE', debe estar en la posición correcta.");
                agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), " Si se incluye 'WHERE', "
                        + "debe estar en la posición correcta");
            }
        }
    }

    // Verifica si un string es un identificador válido
    private boolean esIdentificadorValido(String identificador) {
        // Implementa la lógica de validación de identificador (puede ser alfanumérico, sin números al principio, etc.)
        return identificador.matches("[A-Za-z_][A-Za-z0-9_]*");
    }
    // Procesar la sentencia DELETE

    private void procesarDelete() {
        coincidir("DELETE"); // Coincide con la palabra clave DELETE

        if (!tokenActual.equals("FROM")) {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba 'FROM'.");
        }
        coincidir("FROM");

        if (esIdentificador(tokenActual)) {
            coincidir(tokenActual); // Nombre de la tabla
        } else {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador para la tabla.");
        }

        // Verificar si hay un 'WHERE' opcional
        if (tokenActual != null && tokenActual.equals("WHERE")) {
            procesarWhere(); // Procesar cláusula WHERE si existe
        } else if (tokenActual != null && !tokenActual.equals(";")) {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba 'WHERE' o ';'.");
        }

        coincidir(";"); // Fin de la sentencia
    }

    private void obtenerSiguienteToken() {
        int indice = 0;
        if (indice < tokens.size() - 1) {
            indice++;
            tokenActual = tokens.get(indice);
        } else {
            tokenActual = null;
        }
    }

    private void procesarSelect() {
        coincidir("SELECT"); // Coincide con la palabra clave SELECT

        // Selección de columnas (puede ser *, un identificador, una función agregada, o con alias)
        procesarSeleccionDeColumna();

        // Verificación de la secuencia de "FROM"
        if (tokenActual == null || !tokenActual.equals("FROM")) {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba 'FROM'.");
        }
        coincidir("FROM");

        if (esIdentificador(tokenActual)) {
            coincidirIdentificador(); // Identificador de la tabla
        } else {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un identificador para la tabla.");
        }

        // Sentencias opcionales (JOIN, WHERE, GROUP BY, ORDER BY, LIMIT)
        while (tokenActual != null && (tokenActual.equals("JOIN") || tokenActual.equals("WHERE")
                || tokenActual.equals("GROUP") || tokenActual.equals("ORDER")
                || tokenActual.equals("LIMIT"))) {
            if (tokenActual.equals("JOIN")) {
                procesarJoin();
            } else if (tokenActual.equals("WHERE")) {
                procesarWhere();
            } else if (tokenActual.equals("GROUP")) {
                procesarGroupBy();
            } else if (tokenActual.equals("ORDER")) {
                procesarOrderBy();
            } else if (tokenActual.equals("LIMIT")) {
                procesarLimit();
            }
        }

        if (tokenActual == null || !tokenActual.equals(";")) {
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un punto y coma ';' al final de la sentencia SELECT.");
        } else {
            coincidir(";");
        }
    }

    // Procesar la selección de columnas en el SELECT
    private void procesarSeleccionDeColumna() {
        do {
            if (tokenActual.equals("*")) {
                coincidir("*");
            } else if (esFuncionAgregacion(tokenActual)) {
                procesarFuncionAgregacion(); // Procesar funciones agregadas como SUM, AVG, etc.
            } else if (esIdentificador(tokenActual)) {
                coincidirIdentificador(); // Identificador de la columna

                // Verificar si hay un alias con AS
                if (tokenActual.equals("AS")) {
                    coincidir("AS");
                    coincidirIdentificador(); // Alias
                }
            } else {
                // throw new RuntimeException("Error: Se esperaba '*', un identificador o una función agregada.");
                agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), " Se esperaba '*', un identificador"
                        + " o una función agregada");
            }

            // Si hay una coma, procesamos más columnas
            if (tokenActual.equals(",")) {
                coincidir(",");
            } else {
                break;
            }
        } while (true);
    }

    // Procesar las funciones de agregación (como SUM, AVG, COUNT, etc.)
    private void procesarFuncionAgregacion() {
        String funcion = tokenActual;
        coincidir(funcion);  // Coincidir con la función (ej. SUM, AVG, etc.)
        coincidir("(");
        coincidirIdentificador();  // Debe ser un identificador dentro de la función agregada
        coincidir(")");

        // Verificar si hay un alias con AS
        if (tokenActual.equals("AS")) {
            coincidir("AS");
            coincidirIdentificador(); // Alias de la función agregada
        }
    }

    private void procesarJoin() {
        coincidir("JOIN");
        coincidirIdentificador();  // Primera tabla
        coincidir("ON");
        coincidirIdentificador();  // Identificador tabla1
        coincidir(".");
        coincidirIdentificador();  // Identificador columna1
        coincidir("=");
        coincidirIdentificador();  // Identificador tabla2
        coincidir(".");
        coincidirIdentificador();  // Identificador columna2
    }

    private void procesarOrderBy() {
        coincidir("ORDER");
        coincidir("BY");
        coincidirIdentificador();
        if (tokenActual.equals("ASC") || tokenActual.equals("DESC")) {
            coincidir(tokenActual);  // Ascendente o descendente
        }
    }

    private void procesarLimit() {
        coincidir("LIMIT");
        coincidirEntero();  // Número entero
    }

    // Procesar la cláusula GROUP BY
    private void procesarGroupBy() {
        coincidir("GROUP");
        coincidir("BY");

        // Procesar las columnas en el GROUP BY (puede haber múltiples columnas)
        do {
            coincidirIdentificador(); // Procesar columna en GROUP BY

            if (tokenActual.equals(",")) {
                coincidir(",");
            } else {
                break;
            }
        } while (true);
    }

    // Método para coincidir con un identificador (puedes usarlo para identificadores de tablas y columnas)
    private void coincidirIdentificador() {
        if (esIdentificador(tokenActual)) {
            coincidir(tokenActual);  // Coincidir con el identificador actual
        } else {
            // throw new RuntimeException("Error: Se esperaba un identificador pero se encontró '" + tokenActual + "'.");
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), " Se esperaba un identificador");
        }
    }
// Método para coincidir con un entero (para el LIMIT)

    private void coincidirEntero() {
        if (tokenActual.matches("\\d+")) {
            coincidir(tokenActual);  // Coincidir con el entero actual
        } else {
            //throw new RuntimeException("Error: Se esperaba un número entero pero se encontró '" + tokenActual + "'.");
            agregarErrorSintactico(tokenActual, lineaActual(), columnaActual(), "Se esperaba un número entero");
        }
    }

    private boolean esFuncionAgregacion(String token) {
        // Lista de funciones de agregación SQL válidas
        return token != null && (token.equals("SUM") || token.equals("AVG") || token.equals("COUNT")
                || token.equals("MIN") || token.equals("MAX"));
    }

    // Método para agregar errores a la lista
    public void agregarErrorSintactico(String token, int linea, int columna, String descripcion) {
        erroresSintactico.add(new ErrorSintatico(token, linea, columna, descripcion));
    }

    private int lineaActual() {
        // Retornar el número de línea actual
        return 0;  // Reemplazar con lógica para obtener la línea actual
    }

    private int columnaActual() {
        // Retornar el número de columna actual
        return 0;  // Reemplazar con lógica para obtener la columna actual
    }

    public static List<Tabla> getTablas() {
        return tablas;
    }

    public static List<Modificador> getModificadores() {
        return modificadores;
    }

    public List<ErrorSintatico> getListaErrores() {
        return erroresSintactico;
    }

    // Método para obtener el reporte
    public ReporteNumeroOperaciones getReporteOperaciones() {
        return reporteOperaciones;
    }

    public ReporteTablasModificadas getReporteModificacion() {
        return reporteOperacionesModificacion;
    }
}
