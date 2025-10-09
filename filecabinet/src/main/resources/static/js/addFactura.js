const API_URL = '/api/facturas'; 
let detailCounter = 1; // Contador inicializado al índice del primer elemento

/**
 * Función para clonar y añadir una nueva línea de detalle (DetalleDocumentoDto)
 * al contenedor. Se encarga de actualizar IDs, limpiar valores y añadir el botón de eliminar.
 */
function addDetailRow() {
    detailCounter++;
    const container = document.getElementById('detallesContainer');
    
    // Usamos el primer elemento como plantilla (debe existir un .detalle-item inicial)
    const original = container.querySelector('.detalle-item');
    
    // 1. Clonar el elemento y asignarle un nuevo índice
    const clone = original.cloneNode(true);
    clone.setAttribute('data-index', detailCounter);
    
    // 2. Limpiar valores y actualizar atributos (IDs y nombres) para que sean únicos
    clone.querySelectorAll('input').forEach(input => {
        // Obtenemos el prefijo (ej: 'detalle-descripcion')
        const baseName = input.name.split('-').slice(0, -1).join('-'); 
        input.name = `${baseName}-${detailCounter}`;
        
        const baseId = input.id.split('-').slice(0, -1).join('-');
        input.id = `${baseId}-${detailCounter}`;

        // Limpiar valor. Si es number, lo inicializamos en 0.00, si no, en vacío.
        input.value = (input.type === 'number') ? '0.00' : '';
    });

    // 3. Eliminar la nota de "demo" si está presente en el clon
    const note = clone.querySelector('#detalle-note');
    if (note) note.remove();
    
    // 4. Asegurarse de que el clon no tenga el botón de eliminar del original (si existiera)
    clone.querySelector('.btn-delete-detail')?.parentElement.remove();
    
    // 5. Agregar un botón de eliminar a la nueva fila
    const deleteBtn = document.createElement('button');
    deleteBtn.type = 'button';
    deleteBtn.className = 'btn-delete-detail';
    deleteBtn.textContent = 'Eliminar Detalle';
    deleteBtn.onclick = () => {
        // Usamos la función remove() para eliminar el detalle completo
        clone.remove();
    };
    
    // Creamos un div contenedor para el botón de eliminar
    const deleteDiv = document.createElement('div');
    deleteDiv.className = 'form-group'; 
    deleteDiv.style.textAlign = 'right'; // Alineación a la derecha
    deleteDiv.appendChild(deleteBtn);
    clone.appendChild(deleteDiv);

    // 6. Insertar el clon en el contenedor
    container.appendChild(clone);
}

// Listener para el nuevo botón "Añadir Detalle"
document.getElementById('btnAddDetail').addEventListener('click', addDetailRow);

/**
 * Lógica principal para el envío del formulario Factura
 */
document.getElementById('facturaForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const form = event.target;
    const messageElement = document.getElementById('message');
    
    // 1. Mostrar estado "Enviando"
    messageElement.textContent = 'Enviando...';
    messageElement.style.backgroundColor = '#ecf0f1';
    messageElement.style.color = '#34495e';

    // 2. Recoger datos de los detalles (DetalleDocumentoDto list)
    const detalles = [];
    document.querySelectorAll('.detalle-item').forEach(item => {
        // Buscamos los inputs usando el prefijo de su atributo name
        const descripcionInput = item.querySelector('[name^="detalle-descripcion-"]');
        const cantidadInput = item.querySelector('[name^="detalle-cantidad-"]');
        const precioInput = item.querySelector('[name^="detalle-precioUnitario-"]');

        // Solo añadimos el detalle si la descripción no está vacía
        if (descripcionInput && descripcionInput.value.trim()) {
            detalles.push({
                descripcion: descripcionInput.value,
                cantidad: parseInt(cantidadInput.value) || 0, // Asegura que es un número entero
                precioUnitario: parseFloat(precioInput.value) || 0.00 // Asegura que es un flotante
            });
        }
    });
    
    // 3. Recoger datos del encabezado (FacturaDto)
    const facturaData = {
        numero: form.numero.value,
        fechaEmision: form.fechaEmision.value,
        cliente: form.cliente.value,
        total: parseFloat(form.total.value) || 0.00, 
        detalles: detalles // Envía la lista dinámica de detalles
    };

    // 4. Llamada Fetch al API
    fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(facturaData)
    })
    .then(response => {
        if (response.ok && response.status === 201) {
            response.json().then(data => {
                messageElement.style.backgroundColor = '#e6ffe6'; // Éxito (verde)
                messageElement.style.color = '#27ae60';
                messageElement.textContent = `¡Factura creada con éxito! ID: ${data.id}, Número: ${data.numero}`;
                form.reset();
                // Opcional: Recargar o limpiar solo los detalles agregados para dejar solo la plantilla inicial
            });
        } else if (response.status === 400) {
            response.json().then(errorData => {
                messageElement.style.backgroundColor = '#fcebeb'; // Error (rojo)
                messageElement.style.color = '#c0392b';
                const errorMessage = errorData.message || JSON.stringify(errorData);
                messageElement.textContent = `Error de Validación (400): ${errorMessage}`;
            }).catch(() => {
                // Fallback para errores 400 sin JSON válido
                messageElement.style.backgroundColor = '#fcebeb';
                messageElement.style.color = '#c0392b';
                messageElement.textContent = `Error 400: Solicitud incorrecta.`;
            });
        } else {
            // Otros errores de servidor (500, etc.)
            messageElement.style.backgroundColor = '#fcebeb';
            messageElement.style.color = '#c0392b';
            messageElement.textContent = `Error al crear la factura. Código: ${response.status}`;
        }
    })
    .catch(error => {
        // Errores de red
        console.error('Error:', error);
        messageElement.style.backgroundColor = '#fcebeb';
        messageElement.style.color = '#c0392b';
        messageElement.textContent = 'Error de conexión con el servidor.';
    });
});