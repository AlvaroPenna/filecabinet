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
                    option.textContent = cliente.cifNif + "-" + cliente.nombre + " " + cliente.apellidos;
                    selectCliente.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error al cargar clientes:', error);
            });
    }

    cargarClientes();
    document.getElementById('proyecto-form').addEventListener('submit', function(event) {
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
                alert('Error al guardar el proyecto. Por favor, revisa los datos.');
                console.error('Error al guardar el proyecto:', response.statusText);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Ocurrió un error al conectar con el servidor.');
        });
    });
});