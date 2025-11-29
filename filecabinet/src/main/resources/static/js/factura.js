const API_URL = '/api/facturas';
let detailCounter = 1; // Contador para rastrear el índice de los detalles

// ====================================================================
// === FUNCIONES DE NAVEGACIÓN ========================================
// ====================================================================

function volverAlIndex() {
    // Redirige a la ruta raíz (o /index si tienes ese mapeo específico en Spring Boot)
    window.location.href = "/index"; 
}

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

// ====================================================================
// === FUNCIONES DE CÁLCULO ===========================================
// ====================================================================

/**
 * Calcula y actualiza el subtotal para un detalle específico (Cantidad * Precio Unitario).
 * @param {HTMLElement} itemRow El elemento '.detalle-item' de la fila.
 */
function calculateDetailTotal(itemRow) {
    // 1. Obtener valores de la fila actual
    const cantidadInput = itemRow.querySelector('[name^="detalle-cantidad-"]');
    const precioInput = itemRow.querySelector('[name^="detalle-precioUnitario-"]');
    const subtotalInput = itemRow.querySelector('[name^="detalle-subtotal-"]');

    // Convertir a número, usando 0 si el campo está vacío
    const cantidad = parseFloat(cantidadInput?.value) || 0;
    const precio = parseFloat(precioInput?.value) || 0.00;

    // 2. Calcular subtotal
    const subtotal = cantidad * precio;

    // 3. Actualizar el campo de subtotal (solo lectura)
    if (subtotalInput) {
        subtotalInput.value = subtotal.toFixed(2);
    }

    // 4. Recalcular el total general de la factura
    calculateGrandTotal();
}

/**
 * Recalcula el total general de la factura sumando todos los subtotales.
 */
function calculateGrandTotal() {
    let grandTotal = 0.00;
    
    // Sumar todos los subtotales de las filas existentes
    document.querySelectorAll('.detalle-item').forEach(item => {
        const subtotalInput = item.querySelector('[name^="detalle-subtotal-"]');
        if (subtotalInput) {
            grandTotal += parseFloat(subtotalInput.value) || 0.00;
        }
    });
    
    // Actualizar el campo 'total' de la cabecera (que es readonly)
    const totalInput = document.getElementById('total');
    if (totalInput) {
        totalInput.value = grandTotal.toFixed(2);
    }
}

/**
 * Agrega los listeners de cálculo a los inputs de Cantidad y Precio de una fila.
 * Se activan al cambiar o al escribir.
 * @param {HTMLElement} itemRow El elemento '.detalle-item' de la fila.
 */
function addCalculationListeners(itemRow) {
    const cantidadInput = itemRow.querySelector('[name^="detalle-cantidad-"]');
    const precioInput = itemRow.querySelector('[name^="detalle-precioUnitario-"]');
    
    const handler = () => calculateDetailTotal(itemRow);
    
    if (cantidadInput) cantidadInput.addEventListener('input', handler);
    if (precioInput) precioInput.addEventListener('input', handler);
}

// ====================================================================
// === FUNCIONES DE ESTRUCTURA DINÁMICA ===============================
// ====================================================================

/**
 * Función para clonar y añadir una nueva línea de detalle (DetalleDocumentoDto).
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
    clone.querySelectorAll('input, select').forEach(input => {
        const parts = input.name ? input.name.split('-') : [];
        const baseName = parts.slice(0, -1).join('-'); 
        if (input.name) input.name = `${baseName}-${detailCounter}`;
        
        const idParts = input.id ? input.id.split('-') : [];
        const baseId = idParts.slice(0, -1).join('-');
        if (input.id) input.id = `${baseId}-${detailCounter}`;

        // Limpiar valor. Si es number, lo inicializamos en 0.00, si no, en vacío.
        if (input.type === 'number') {
            input.value = '0.00';
        } else if (input.tagName === 'INPUT') {
            input.value = '';
        }
    });

    // 3. Eliminar la nota de "demo" si está presente en el clon
    const note = clone.querySelector('#detalle-note');
    if (note) note.remove();
    
    // 4. Eliminar cualquier botón de eliminar que venga del original y reemplazar
    const existingDeleteDiv = clone.querySelector('.btn-delete-detail')?.parentElement;
    if (existingDeleteDiv) existingDeleteDiv.remove();

    // 5. Agregar un botón de eliminar a la nueva fila
    const deleteBtn = document.createElement('button');
    deleteBtn.type = 'button';
    deleteBtn.className = 'btn-delete-detail';
    deleteBtn.textContent = 'Eliminar Detalle';
    deleteBtn.onclick = () => {
        clone.remove();
        calculateGrandTotal(); // Recalcular el total después de eliminar una fila
    };
    
    const deleteDiv = document.createElement('div');
    deleteDiv.className = 'form-group detail-actions'; 
    deleteDiv.style.textAlign = 'right';
    deleteDiv.appendChild(deleteBtn);
    clone.appendChild(deleteDiv);

    // 6. Insertar el clon en el contenedor y añadir listeners
    container.appendChild(clone);
    addCalculationListeners(clone); // Añadir listeners de cálculo al nuevo clon
}

// ====================================================================
// === LISTENERS PRINCIPALES ==========================================
// ====================================================================

// Listener para el nuevo botón "Añadir Detalle"
const btnAddDetail = document.getElementById('btnAddDetail');
if (btnAddDetail) {
    btnAddDetail.addEventListener('click', addDetailRow);
}

// Inicializar listeners y cálculos al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    cargarClientes();
    const originalItem = document.querySelector('.detalle-item');
    if (originalItem) {
        // Asegura que el elemento original tenga el data-index=1 y los listeners
        originalItem.setAttribute('data-index', '1');
        addCalculationListeners(originalItem);
    }
    
    // Añadir listeners de cálculo a todas las filas existentes (en caso de precarga de datos)
    document.querySelectorAll('.detalle-item').forEach(item => {
        addCalculationListeners(item);
    });

    // Calcular el total inicial (por si hay datos precargados)
    calculateGrandTotal(); 
});


// ====================================================================
// === LÓGICA PRINCIPAL DE ENVÍO DEL FORMULARIO =======================
// ====================================================================

const facturaForm = document.getElementById('facturaForm');
if (facturaForm) {
    facturaForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const form = event.target;
        const messageElement = document.getElementById('message');
        
        // 1. Mostrar estado "Enviando"
        messageElement.textContent = 'Enviando...';
        messageElement.style.backgroundColor = '#ecf0f1';
        messageElement.style.color = '#34495e';

        // 2. Recoger datos de los detalles
        const detalles = [];
        document.querySelectorAll('.detalle-item').forEach(item => {
            const descripcionInput = item.querySelector('[name^="detalle-descripcion-"]');
            const cantidadInput = item.querySelector('[name^="detalle-cantidad-"]');
            const precioInput = item.querySelector('[name^="detalle-precioUnitario-"]');

            if (descripcionInput && descripcionInput.value.trim()) {
                detalles.push({
                    descripcion: descripcionInput.value,
                    cantidad: parseFloat(cantidadInput?.value) || 0, 
                    precioUnitario: parseFloat(precioInput?.value) || 0.00
                });
            }
        });
        
        // 3. Recoger datos del encabezado
        const facturaData = {
            // Asumiendo que 'numero' y 'cliente' están en el formulario
            numero: form.numero?.value || '',
            fechaEmision: form.fechaEmision?.value || '',
            cliente: form.cliente?.value || '',
            // El campo total es readonly, se usa su valor calculado
            total: parseFloat(form.total?.value) || 0.00, 
            detalles: detalles
        };

        // 4. Llamada Fetch al API
        fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(facturaData)
        })
        .then(response => {
            // Manejo de respuesta
            if (response.ok && response.status === 201) {
                // Éxito
                response.json().then(data => {
                    messageElement.style.backgroundColor = '#e6ffe6';
                    messageElement.style.color = '#27ae60';
                    messageElement.textContent = `¡Factura creada con éxito! ID: ${data.id}, Número: ${data.numero}`;
                    form.reset();
                });
            } else {
                // Error
                response.json().then(errorData => {
                    const errorMessage = errorData.message || JSON.stringify(errorData);
                    messageElement.style.backgroundColor = '#fcebeb';
                    messageElement.style.color = '#c0392b';
                    messageElement.textContent = `Error (${response.status}): ${errorMessage}`;
                }).catch(() => {
                    messageElement.style.backgroundColor = '#fcebeb';
                    messageElement.style.color = '#c0392b';
                    messageElement.textContent = `Error ${response.status}: Solicitud incorrecta.`;
                });
            }
        })
        .catch(error => {
            console.error('Error de red:', error);
            messageElement.style.backgroundColor = '#fcebeb';
            messageElement.style.color = '#c0392b';
            messageElement.textContent = 'Error de conexión con el servidor.';
        });
    });
}