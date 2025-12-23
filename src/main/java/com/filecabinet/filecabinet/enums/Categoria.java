package com.filecabinet.filecabinet.enums;

public enum Categoria {
    DUEÑO("Dueño"),
    JEFE_DE_OBRA("Jefe de Obra"),
    ENCARGADO_GENERAL("Encargado General"),
    OFICIAL_DE_PRIMERA("Oficial de Primera"),
    OFICIAL_DE_SEGUNDA("Oficial de Segunda"),
    PEON_ESPECIALISTA("Peón Especialista"),
    PEON_ORDINARIO("Peón Ordinario"),
    INFORMATICO("Informático");

    private final String nombreLegible;

    Categoria(String nombreLegible) {
        this.nombreLegible = nombreLegible;
    }

    public String getNombreLegible() {
        return nombreLegible;
    }
}
