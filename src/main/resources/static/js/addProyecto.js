function volverAlIndex() {
    window.location.href = "/index";
}
document.addEventListener('DOMContentLoaded', () => {

    function cargarClientes() {
        fetch('http://localhost:8080/api/clientes')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al obtener la lista de clientes');
                }
                return response.json();
            })
            .then(clientes => {
                const selectCliente = document.getElementById('cliente');
                selectCliente.innerHTML = '';

                const defaultOption = document.createElement('option');
                defaultOption.value = '';
                defaultOption.textContent = 'Seleccione un cliente (opcional)';
                selectCliente.appendChild(defaultOption);

                clientes.forEach(cliente => {
                    const option = document.createElement('option');
                    option.value = cliente.id;
                    option.textContent = cliente.cif + "-" + cliente.nombre + " " + cliente.apellidos;
                    selectCliente.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error al cargar clientes:', error);
            });
    }

    cargarClientes();
    document.getElementById('proyecto-form').addEventListener('submit', function (event) {
        event.preventDefault();

        const nombre = document.getElementById('nombre').value;
        const direccion = document.getElementById('direccion').value;
        const ciudad = document.getElementById('ciudad').value;
        const codigoPostal = document.getElementById('codigoPostal').value;
        const fechaInicio = document.getElementById('fechaInicio').value;
        const fechaFin = document.getElementById('fechaFin').value;
        const clienteId = document.getElementById('cliente').value;

        const proyectoData = {
            nombre: nombre,
            direccion: direccion,
            ciudad: ciudad,
            codigoPostal: codigoPostal,
            fechaInicio: fechaInicio,
            fechaFin: fechaFin,
            cliente_id: clienteId ? parseInt(clienteId) : null
        };

        fetch('http://localhost:8080/api/proyectos', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(proyectoData)
        })
            .then(response => {
                if (response.ok) {
                    alert('Proyecto guardado con éxito! ✅');
                    document.getElementById('proyecto-form').reset();
                } else {
                    if (response.status === 409) {
                        // 2. Intentar leer el cuerpo de la respuesta para obtener el mensaje específico
                        return response.json()
                            .then(errorBody => {
                                // Mostrar el mensaje de error específico (ej: "La factura con número X ya ha sido registrada.")
                                alert('⚠️ Error de Duplicado: ' + errorBody.error);
                                console.error('Error de servidor (409 Conflict):', errorBody.error);
                                document.getElementById('proyecto-form').reset();
                            })
                            .catch(() => {
                                // Manejar si la respuesta 409 no tiene un cuerpo JSON válido
                                alert('Error: El proyecto ya fue registrado. (Código 409)');
                            });
                    }

                    // 3. Si es otro error general de servidor (400, 500, etc.)
                    else {
                        // Para otros errores, lee el texto del estado HTTP si no puedes leer el cuerpo
                        console.error('Error al guardar el proyecto:', response.status, response.statusText);
                        alert(`Error al guardar el proyecto (Código ${response.status}). Por favor, revisa los datos.`);
                        document.getElementById('proyecto-form').reset();
                    }
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Ocurrió un error al conectar con el servidor.');
            });
    });
});