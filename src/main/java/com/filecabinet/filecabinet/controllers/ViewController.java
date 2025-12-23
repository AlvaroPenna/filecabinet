package com.filecabinet.filecabinet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/gastos")
    public String mostrarListaGastos() {
        return "gastos_list"; 
    }
    
    @GetMapping("/gasto/new")
    public String mostrarFormularioGasto() {
        return "addGastos"; 
    }
    
    @GetMapping("/facturas")
    public String mostrarListaFacturas() {
        return "facturas_list"; 
    }

    @GetMapping("/factura/new")
    public String mostrarFormularioFactura() { 
        return "addFactura";
    }

    @GetMapping("/cliente/new")
    public String mostrarFormularioCliente() {
      return "addCliente";  
    }

    @GetMapping("/empleado/new")
    public String mostrarFormularioTrabajador(){
        return "addEmpleado";
    }

    @GetMapping("/proyecto/new")
    public String monstrarFormularioProyecto(){
        return "addProyecto";
    }

    @GetMapping("/proyectoEmpleado/new")
    public String monstrarFormularioProyectoEmpleado(){
        return "addProyectoEmpleo";
    }

    @GetMapping("/presupuesto/new")
    public String mostrarFormularioPresupuesto() {
        return "addPresupuesto";
    }

}
