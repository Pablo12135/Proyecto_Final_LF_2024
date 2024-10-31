package Clases_Utilizar;


public class Modificador {

     private String tipo;
    private String nombreTabla;
    private String nombreColumna;
    private String tipodato;

    public Modificador(String tipo, String nombreTabla, String nombreColumna, String tipodato) {
        this.tipo = tipo;
        this.nombreTabla = nombreTabla;
        this.nombreColumna = nombreColumna;
        this.tipodato = tipodato;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNombreTabla() {
        return nombreTabla;
    }

    public String getNombreColumna() {
        return nombreColumna;
    }
    
    public String getTipoMo() {
        return tipodato;
    }
}