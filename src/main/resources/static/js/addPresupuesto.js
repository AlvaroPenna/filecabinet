const API_URL = '/api/presupuestos';
let detailCounter = 1;

function calculateDetailTotal(itemRow) {
    const cantidadInput = itemRow.querySelector('[name*="detalle-cantidad"]');
    const precioInput = itemRow.querySelector('[name*="detalle-precioUnitario"]');
    const subtotalInput = itemRow.querySelector('[name*="detalle-subtotal"]');

    const cantidad = parseFloat(cantidadInput?.value) || 0;
    const precio = parseFloat(precioInput?.value) || 0;
    const subtotal = cantidad * precio;

    if (subtotalInput) {
        subtotalInput.value = subtotal.toFixed(2);
    }

    calculateGrandTotal();
}

function calculateGrandTotal() {
    let grandTotal = 0.00;
    document.querySelectorAll('.detalle-item').forEach(item => {
        const subtotalInput = item.querySelector('[name*="detalle-subtotal"]');
        if (subtotalInput) {
            grandTotal += parseFloat(subtotalInput.value) || 0.00;
        }
    });

    const totalInput = document.getElementById('total_bruto');
    if (totalInput) {
        totalInput.value = grandTotal.toFixed(2);
    }
}

function addCalculationListeners(itemRow) {
    const inputs = itemRow.querySelectorAll('[name*="detalle-cantidad"], [name*="detalle-precioUnitario"]');
    inputs.forEach(input => {
        input.addEventListener('input', () => calculateDetailTotal(itemRow));
    });
}

function addDetailRow() {
    detailCounter++;
    const container = document.getElementById('detallesContainer');
    const original = container.querySelector('.detalle-item');

    // Clonar fila
    const clone = original.cloneNode(true);
    clone.setAttribute('data-index', detailCounter);

    // Limpiar campos y actualizar nombres/IDs
    clone.querySelectorAll('input, select').forEach(input => {
        input.value = (input.type === 'number') ? '0.00' : '';

        if (input.name) {
            const baseName = input.name.split('-').slice(0, 2).join('-');
            input.name = `${baseName}-${detailCounter}`;
        }
        if (input.id) {
            const baseId = input.id.split('-').slice(0, 2).join('-');
            input.id = `${baseId}-${detailCounter}`;
        }
    });

    // Gestionar botón de eliminar
    let deleteBtn = clone.querySelector('.btn-delete-detail');
    if (!deleteBtn) {
        const deleteDiv = document.createElement('div');
        deleteDiv.className = 'form-group detail-actions';
        deleteDiv.style.textAlign = 'right';
        deleteDiv.innerHTML = '<button type="button" class="btn-delete-detail">Eliminar Detalle</button>';
        clone.appendChild(deleteDiv);
        deleteBtn = deleteDiv.querySelector('button');
    }

    deleteBtn.onclick = () => {
        clone.remove();
        calculateGrandTotal();
    };

    container.appendChild(clone);
    addCalculationListeners(clone);
}

const presupuestoForm = document.getElementById('presupuestoForm');
if (presupuestoForm) {
    presupuestoForm.addEventListener('submit', function (event) {
        event.preventDefault();
        const messageElement = document.getElementById('message');
        messageElement.textContent = 'Enviando...';

        // 1. Recoger TODOS los detalles
        const detalles = [];
        document.querySelectorAll('.detalle-item').forEach(item => {
            const trabajo = item.querySelector('[name*="detalle-trabajo"]')?.value;
            const descripcion = item.querySelector('[name*="detalle-descripcion"]')?.value;
            const cantidad = parseFloat(item.querySelector('[name*="detalle-cantidad"]')?.value) || 0;
            const precio = parseFloat(item.querySelector('[name*="detalle-precioUnitario"]')?.value) || 0;

            if (descripcion && descripcion.trim() !== "") {
                detalles.push({
                    trabajo: trabajo,
                    descripcion: descripcion,
                    cantidad: cantidad,
                    precioUnitario: precio
                });
            }
        });

        // 2. Calcular impuestos para el JSON
        const totalBruto = parseFloat(document.getElementById('total_bruto').value) || 0.00;
        const tipoIvaRadio = document.querySelector('input[name="tipo_iva"]:checked');
        const porcentajeIva = tipoIvaRadio ? parseFloat(tipoIvaRadio.value) : 21.00;
        const totalIva = totalBruto * (porcentajeIva / 100);
        const totalNeto = totalBruto + totalIva;

        const presuestoData = {
            numPresupuesto: document.getElementById('numero').value,
            estadoAceptacion: 'PENDIENTE',
            fechaAceptacion: null,
            fechaEmision: document.getElementById('fechaEmision').value,
            total_bruto: totalBruto,
            total_iva: totalIva,
            total_neto: totalNeto,
            tipo_iva: porcentajeIva,
            cliente_id: parseInt(document.getElementById('cliente').value) || null,
            proyecto_id: parseInt(document.getElementById('proyecto').value) || null,
            detalles: detalles
        };

        console.log("Enviando JSON:", presuestoData);

        fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(presuestoData)
        })
            .then(response => {
                if (response.ok) {
                    alert("Presupuesto guardao correctamente");
                    window.location.reload();
                } else {
                    throw new Error("Error en el servidor");
                }
            })
            .catch(error => {
                console.error(error);
                messageElement.textContent = 'Error al guardar.';
            });
    });
}

// ====================================================================
// === CARGA DE DATOS INICIALES =======================================
// ====================================================================

function cargarProyectos() {
    fetch('http://localhost:8080/api/proyectos', {
        method: 'GET',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al obtener la lista de trabajadores: ' + response.statusText);
            }
            return response.json();
        })
        .then(proyectos => {
            const selectProyecto = document.getElementById('proyecto');
            selectProyecto.innerHTML = '';
            const defaultOption = document.createElement('option');
            defaultOption.value = '';
            defaultOption.textContent = 'Selecciona un proyecto...';
            selectProyecto.appendChild(defaultOption);
            proyectos.forEach(proyecto => {
                const option = document.createElement('option');
                option.value = proyecto.id;
                option.textContent = proyecto.id + proyecto.nombre;
                selectProyecto.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error al cargar proyectos:', error);
            const selectTrabajador = document.getElementById('proyecto');
            selectTrabajador.innerHTML = `<option value="">Error al cargar (Ver consola)</option>`;
        });

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
                option.textContent = cliente.cif + "-" + cliente.nombre + " " + cliente.apellidos;
                selectCliente.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error al cargar clientes:', error);
        });
}

document.addEventListener('DOMContentLoaded', () => {
    cargarProyectos();
    cargarClientes();

    // Activar cálculos en la primera fila existente
    const primeraFila = document.querySelector('.detalle-item');
    if (primeraFila) addCalculationListeners(primeraFila);

    const btnAddDetail = document.getElementById('btnAddDetail');
    if (btnAddDetail) btnAddDetail.addEventListener('click', addDetailRow);
});

function volverAlIndex() {
    window.location.href = "/index";
}