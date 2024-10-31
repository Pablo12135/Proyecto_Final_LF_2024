package Clases_Utilizar;

public class Columna {

    private String nombre;
    private String tipoDato;
    private boolean esLlave; // Indica si es una llave primaria
    private boolean esUnica; // Indica si tiene restricción UNIQUE
    private boolean esNotNull; // Indica si tiene restricción NOT NULL
    private String foreignKeyColumn; // Columna de la llave foránea
    private String referencedTable; // Tabla referenciada
    private String referencedColumn; // Columna referenciada

    // Constructor
    public Columna(String nombre, String tipoDato, boolean esLlave) {
        this.nombre = nombre;
        this.tipoDato = tipoDato;
        this.esLlave = esLlave;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public boolean isLlave() {
        return esLlave;
    }

    public boolean isUnica() {
        return esUnica;
    }

    public boolean isNotNull() {
        return esNotNull;
    }

    public String getForeignKeyColumn() {
        return foreignKeyColumn;
    }

    public String getReferencedTable() {
        return referencedTable;
    }

    public String getReferencedColumn() {
        return referencedColumn;
    }

    // Setters
    public void setUnique(boolean esUnica) {
        this.esUnica = esUnica;
    }

    public void setNotNull(boolean esNotNull) {
        this.esNotNull = esNotNull;
    }

    public void setForeignKeyColumn(String foreignKeyColumn) {
        this.foreignKeyColumn = foreignKeyColumn;
    }

    public void setReferencedTable(String referencedTable) {
        this.referencedTable = referencedTable;
    }

    public void setReferencedColumn(String referencedColumn) {
        this.referencedColumn = referencedColumn;
    }

    // Método para mostrar información de la columna
    @Override
    public String toString() {
        return "Columna{"
                + "nombre='" + nombre + '\''
                + ", tipoDato='" + tipoDato + '\''
                + ", esLlave=" + esLlave
                + ", esUnica=" + esUnica
                + ", esNotNull=" + esNotNull
                + ", foreignKeyColumn='" + foreignKeyColumn + '\''
                + ", referencedTable='" + referencedTable + '\''
                + ", referencedColumn='" + referencedColumn + '\''
                + '}';
    }
}
