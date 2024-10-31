package com.mycompany.proyecto_final_202lf_202230094;

//Seccion de imports
import java.util.ArrayList;
import java.util.List;
import Reportes.ErrorLexico;
import Clases_Utilizar.Token;

%%
%{

// Codigo Java

    private List<String> lista = new ArrayList<>();
    // Lista para almacenar los tokens como objetos
    private List<Token> listaTokens = new ArrayList<>();
    private List<ErrorLexico> listaErrores = new ArrayList<>();

    public void addList(String token) {
        lista.add(token);
    }

    // Método para agregar errores léxicos a la lista de errores
    public void addListaErrores(ErrorLexico error) {
        listaErrores.add(error);  // Agregar el objeto ErrorLexico a la lista
    }

// Método para agregar tokens a la lista de objetos Token
    public void addToken(String tokenText) {
        Token token = new Token(tokenText, yyline, yycolumn);  // Crear un objeto Token con el texto, línea y columna
        listaTokens.add(token);  // Agregar el objeto Token a la lista
    }

    public List<String> getLista(){
        return lista;
    }
    
    // Método para obtener la lista de tokens
    public List<Token> getListaTokens(){
        return listaTokens;
    }

    public List<ErrorLexico> getListaErrores() {
        return listaErrores;  // Devolver la lista de objetos ErrorLexico
    }



%}
// Configuración
%public
%class Analizador_Lexico
%unicode
%line
%column
%standalone

// Expresiones regulares
DIGITO = [0-9]
ENTERO = {DIGITO}+
LETRA = [a-zA-Z]
IDENTIFICADOR = {LETRA}(_?{LETRA}|{DIGITO})*
ESPACIOS = [ \t\r\n]+
// Expresiones regulares
FECHA_VALIDA = ([1-2][0-9]{3})"-"([0-1][0-9])"-"([0-3][0-9])
FECHA = "'" {FECHA_VALIDA} "'"
CADENA = "'".*"'"
// Comentarios de una línea que comienzan con dos guiones medio seguidos de un espacio
COMENTARIO_LINEA = "--"[^(\r|\n)]*
// Palabras clave
KEYWORDS = "CREATE"|"DATABASE"|"TABLE"|"KEY"|"NULL"|"PRIMARY"|"UNIQUE"|"FOREIGN"|"REFERENCES"|"ALTER"|"ADD"|"COLUMN"|"TYPE"|"DROP"|"CONSTRAINT"|"IF"|"EXIST"|"EXISTS"|"CASCADE"|"ON"|"DELETE"|"SET"|"UPDATE"|"INSERT"|"INTO"|"VALUES"|"SELECT"|"FROM"|"WHERE"|"AS"|"GROUP"|"ORDER"|"BY"|"ASC"|"DESC"|"LIMIT"|"JOIN"
BOOLEANO = "TRUE"|"FALSE"
// Tipos de datos
TIPOS_DATOS = "INTEGER"|"BIGINT"|"VARCHAR"|"DECIMAL"|"DATE"|"TEXT"|"BOOLEAN"|"SERIAL"|"NUMERIC"
// Funciones de agregación
AGREGACION = "SUM"|"AVG"|"COUNT"|"MAX"|"MIN"
// Operadores lógicos, relacionales y aritméticos
OPERADORES_LOGICOS = "AND" | "OR" | "NOT"
OPERADORES_ARITMETICOS = [+\-*/=] 
OPERADORES_RELACIONALES = (<=|>=|<|>)
// Comentarios
COMENTARIO_LINEA = "--"[^(\r|\n)]*
// Símbolos
SIMBOLOS = [\\(\\)\\,;\\.\\=]

%%

// Reglas de escaneo

// Operadores aritméticos
{OPERADORES_ARITMETICOS} {
    System.out.println("OPERADOR ARITMETICO: " + yytext());
    addList(yytext());
    Token token = new Token(yytext(), yyline, yycolumn);
}

// Operadores relacionales
{OPERADORES_RELACIONALES} {
    System.out.println("OPERADOR RELACIONAL: " + yytext());
    addList(yytext());
    Token token = new Token(yytext(), yyline, yycolumn);
}

// Signos
{SIMBOLOS} {
    System.out.println("SIMBOLO: " + yytext());
    addList(yytext());
    Token token = new Token(yytext(), yyline, yycolumn);
}

// Espacios
{ESPACIOS} { /* Ignorar espacios */ }

// Palabras clave
{KEYWORDS} {
    System.out.println("PALABRA CLAVE: " + yytext());
    addList(yytext());
    Token token = new Token(yytext(), yyline, yycolumn);
}

// Tipos de datos
{TIPOS_DATOS} {
    System.out.println("TIPO DE DATO: " + yytext());
    addList(yytext());
    Token token = new Token(yytext(), yyline, yycolumn);
}

// Funciones de agregación
{AGREGACION} {
    System.out.println("FUNCION DE AGREGACION: " + yytext());
    addList(yytext());
    Token token = new Token(yytext(), yyline, yycolumn);
}

// Operadores lógicos
{OPERADORES_LOGICOS} {
    System.out.println("OPERADOR LOGICO: " + yytext());
    addList(yytext());
    Token token = new Token(yytext(), yyline, yycolumn);
}

// Enteros
{ENTERO} {
    System.out.println("ENTERO: " + yytext());
    addList(yytext());
    Token token = new Token(yytext(), yyline, yycolumn);
}

// Decimales
{ENTERO}"."{ENTERO} {
    System.out.println("DECIMAL: " + yytext());
    addList(yytext());
    Token token = new Token(yytext(), yyline, yycolumn);
}

// Booleanos
{BOOLEANO} {
    System.out.println("BOOLEANO: " + yytext());
    addList(yytext());
    Token token = new Token(yytext(), yyline, yycolumn);
}

// Fechas (debe ir antes de las cadenas)
{FECHA} {
    System.out.println("FECHA: " + yytext());
    addList(yytext());
    Token token = new Token(yytext(), yyline, yycolumn);
}

// Cadenas (se evalúa después de fechas)
{CADENA} {
    System.out.println("CADENA: " + yytext());
    addList(yytext());
    Token token = new Token(yytext(), yyline, yycolumn);
}

// Identificadores
{IDENTIFICADOR} {
    String id = yytext();
    // Validar el identificador para asegurarse de que cumple con snake_case
    if (id.matches("^[a-z][a-z0-9_]*$")) {
        System.out.println("IDENTIFICADOR: " + id);
        addList(id);
        Token token = new Token(id, yyline, yycolumn);
    } else {
        // Si no cumple con la nomenclatura snake_case, agregar un error
        String mensajeError = "Identificador no válido. Debe seguir la nomenclatura snake_case (solo letras minúsculas, guiones bajos y números)";
        ErrorLexico error = new ErrorLexico(id, yyline, yycolumn, mensajeError);
        System.out.println("Token: '" + id + "' Línea: " + yyline + " Columna: " + yycolumn + " Descripción: " + mensajeError);
        addListaErrores(error);
    }
}

// Comentarios de una línea
{COMENTARIO_LINEA} {
    System.out.println("COMENTARIO: " + yytext());
    addList(yytext());
    Token token = new Token(yytext(), yyline, yycolumn);
}


// Manejo de errores léxicos

. {
    // Capturar el token que causó el error
    String tokenNoReconocido = yytext();
    
    // Crear el mensaje de error personalizado
    String mensajeError = "";
    
    // Condición para operadores relacionales
    if (tokenNoReconocido.equals("<") || tokenNoReconocido.equals(">") || tokenNoReconocido.equals("<=") || tokenNoReconocido.equals(">=")) {
        mensajeError = "operador no reconocido";
    }
    // Condición para símbolos
    else if (tokenNoReconocido.equals("(") || tokenNoReconocido.equals(")") || tokenNoReconocido.equals(",") || tokenNoReconocido.equals(";") || tokenNoReconocido.equals(".")) {
        mensajeError = "Simbolo no reconocido";
    }
    // Condición para otros caracteres no reconocidos
    else {
        mensajeError = "Caracter no reconocido";
    }

    // Crear el objeto ErrorLexico con la información
    ErrorLexico error = new ErrorLexico(tokenNoReconocido, yyline, yycolumn, mensajeError);

    // Mostrar el mensaje de error en la consola
    System.out.println("Token: '" + tokenNoReconocido + "' Línea: " + yyline + " Columna: " + yycolumn + " Descripción: " + mensajeError);

    // Agregar el error a la lista de errores
    addListaErrores(error);
}