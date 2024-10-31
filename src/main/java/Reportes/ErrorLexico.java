package Reportes;


public class ErrorLexico {

    private String token;
    private int linea;
    private int columna;
    private String descripcion;

    public ErrorLexico(String token, int linea, int columna, String descripcion) {
        this.token = token;
        this.linea = linea;
        this.columna = columna;
        this.descripcion = descripcion;
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

    public String getDescripcion() {
        return descripcion;
    }
    
     @Override
    public String toString() {
        return "Error{"
                + "Token='" + token + '\''
                + ", Linea ='" + linea + '\''
                + ", Columna='" + columna + '\''
                + ", Descripcion=" + descripcion
                + '}';
    }
}
