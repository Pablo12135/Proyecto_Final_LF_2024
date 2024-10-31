package Clases_Utilizar;

public class Token {
private String token;
    private int linea;
    private int columna;

    public Token(String token, int linea, int columna) {
        this.token = token;
        this.linea = linea;
        this.columna = columna;
    }

    public String getToken() {
        return token;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }
     @Override
    public String toString() {
        return "Error{"
                + "Token='" + token + '\''
                + ", Linea ='" + linea + '\''
                + ", Columna='" + columna + '\''
                + '}';
    }
}
