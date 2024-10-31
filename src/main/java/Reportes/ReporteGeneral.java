package Reportes;

public class ReporteGeneral {

    private String token;
    private int linea;
    private int columna;
    private String descripcion;

    public ReporteGeneral(String token, int linea, int columna, String descripcion) {
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
}
