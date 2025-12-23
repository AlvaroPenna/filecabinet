package com.filecabinet.filecabinet.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoEmpleadoDto {

    private Long id;
    private Date dia;
    private Integer horas;
    private String tareaRealizada;
    private Long empleado_id;
    private Long proyecto_id;
    
}
