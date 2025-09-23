document.addEventListener('DOMContentLoaded', () => {
    // Función para cargar los clientes desde la API
    /*function cargarClientes() {
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
                    option.textContent = cliente.nombre;
                    selectCliente.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error al cargar clientes:', error);
            });
    }
    
    cargarClientes();*/

    // Manejo del formulario
    document.getElementById('gasto-form').addEventListener('submit', function(event) {
        event.preventDefault();

        const proveedor = document.getElementById('proveedor').value;
        const fechaInput = document.getElementById('fechaEmision').value;
        const fechaEmision = new Date(fechaInput).toISOString();
        const numero = document.getElementById('numero').value;
        const obra = document.getElementById('obra').value;
        const importe_sin_iva = parseFloat(document.getElementById('importe_sin_iva').value);
        const tipo_iva = parseInt(document.querySelector('input[name="tipo_iva"]:checked').value);
        const descripcion = document.getElementById('descripcion').value;
        const clienteId = document.getElementById('cliente').value;
        
        const ivaDecimal = tipo_iva / 100;
        const precioIva = importe_sin_iva * ivaDecimal;
        const precioConIva = importe_sin_iva + precioIva;

        const gastoData = {
            usuario: { id: 1 },
            proveedor: proveedor,
            fechaEmision: fechaEmision,
            numero: numero,
            obra: obra,
            precio_sin_iva: importe_sin_iva,
            precio_iva:precioIva,
            precio_con_iva:precioConIva,
            descripcion: descripcion,
            cliente: clienteId ? { id: parseInt(clienteId) } : null
        };
        
        fetch('http://localhost:8080/api/gastos', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(gastoData)
        })
        .then(response => {
            if (response.ok) {
                alert('¡Gasto guardado con éxito! ✅');
                document.getElementById('gasto-form').reset();
            } else {
                alert('Error al guardar el gasto. Por favor, revisa los datos.');
                console.error('Error al guardar el gasto:', response.statusText);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Ocurrió un error al conectar con el servidor.');
        });
    });
});