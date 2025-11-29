package com.filecabinet.filecabinet.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoTrabajadorDto {

    private Long id;
    private Date dia;
    private Integer horas;
    
}
