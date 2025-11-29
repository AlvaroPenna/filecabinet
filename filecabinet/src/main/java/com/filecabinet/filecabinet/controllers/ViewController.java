package com.filecabinet.filecabinet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    /**
     * Maneja la solicitud GET para la ruta /login y muestra el formulario de inicio de sesión.
     * * @return El nombre de la plantilla (login.html).
     */
    @GetMapping("/login")
    public String login() {
        // Spring Security maneja el POST a /perform_login
        return "login"; 
    }

    /**
     * Maneja la solicitud GET para la ruta principal /.
     * Esta ruta estará protegida por SecurityConfig, por lo que solo los usuarios 
     * autenticados podrán acceder después de un login exitoso.
     * * @return El nombre de la plantilla (index.html).
     */
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/gastos")
    public String mostrarListaGastos() {
        // Devuelve el nombre de la plantilla HTML. 
        // Spring buscará: src/main/resources/templates/gastos_list.html
        return "gastos_list"; 
    }
    
    // Opcional: Para la creación de nuevos gastos
    @GetMapping("/gastos/new")
    public String mostrarFormularioGasto() {
        return "addGastos"; 
    }
    
    @GetMapping("/facturas")
    public String mostrarListaFacturas() {
        // La ruta "/facturas" será mapeada a la plantilla:
        // src/main/resources/templates/facturas_list.html 
        return "facturas_list"; 
    }

    // Opcional: Ruta para el formulario de agregar/crear una nueva factura
    @GetMapping("/facturas/add")
    public String mostrarFormularioFactura() {
        // La ruta "/facturas/add" será mapeada a la plantilla:
        // src/main/resources/templates/addFactura.html 
        return "addFactura";
    }

    @GetMapping("/clientes/new")
    public String mostrarFormularioCliente() {
      return "addCliente";  
    }

    @GetMapping("/trabajador/new")
    public String mostrarFormularioTrabajador(){
        return "addTrabajador";
    }

    @GetMapping("/proyecto/new")
    public String monstrarFormularioProyecto(){
        return "addProyecto";
    }

}
