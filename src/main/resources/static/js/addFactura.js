const API_URL = '/api/facturas';
let detailCounter = 1;
/**
 * Calcula el subtotal de una fila específica
 */
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

/**
 * Suma todos los subtotales para obtener el total bruto
 */
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

/**
 * Vincula los eventos de escucha a los inputs de una fila
 */
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
    // IMPORTANTE: Activar cálculos en la nueva fila
    addCalculationListeners(clone);
}

const facturaForm = document.getElementById('facturaForm');
if (facturaForm) {
    facturaForm.addEventListener('submit', function (event) {
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
                    precioUnitario: precio,
                    subtotal: subtotal
                });
            }
        });

        // 2. Calcular impuestos para el JSON
        const totalBruto = parseFloat(document.getElementById('total_bruto').value) || 0.00;
        const tipoIvaRadio = document.querySelector('input[name="tipo_iva"]:checked');
        const porcentajeIva = tipoIvaRadio ? parseFloat(tipoIvaRadio.value) : 21.00;
        const totalIva = totalBruto * (porcentajeIva / 100);
        const totalNeto = totalBruto + totalIva;

        const facturaData = {
            numFactura: document.getElementById('numero').value,
            estadoPago: 'PENDIENTE',
            fechaEmision: document.getElementById('fechaEmision').value,
            total_bruto: totalBruto,
            total_iva: totalIva,
            total_neto: totalNeto,
            descuento: document.getElementById('descuento').value,
            cliente_id: parseInt(document.getElementById('cliente').value) || null,
            proyecto_id: parseInt(document.getElementById('proyecto').value) || null,
            detalles: detalles
        };

        fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify(facturaData)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error("Error en el servidor");
                }
            })
            .then(async (nuevaFactura) => {
                // 1. Notificar al usuario (opcional, puedes quitarlo si quieres que sea más fluido)
                alert("Factura guardada. Iniciando descarga automática del Excel...");

                // 2. LLAMADA AUTOMÁTICA PARA DESCARGAR EXCEL
                // Usamos 'await' para asegurar que el navegador procese la descarga antes de recargar
                await descargarExcel(nuevaFactura.id);

                // 3. Pequeña pausa de seguridad y recarga
                // A veces los navegadores cortan la descarga si el reload es instantáneo
                setTimeout(() => {
                    window.location.reload();
                }, 1000); // 1 segundo de espera
            })
            .catch(error => {
                console.error(error);
                messageElement.textContent = 'Error al guardar.';
                alert("Hubo un error al guardar la factura");
            });
    });
}

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

    const primeraFila = document.querySelector('.detalle-item');
    if (primeraFila) addCalculationListeners(primeraFila);
    const btnAddDetail = document.getElementById('btnAddDetail');
    if (btnAddDetail) btnAddDetail.addEventListener('click', addDetailRow);
});

function volverAlIndex() {
    window.location.href = "/index";
}

async function descargarExcel(idFactura) {
    try {
        // Llamamos al endpoint que acabamos de asegurar en el Backend
        const response = await fetch(`http://localhost:8080/api/facturas/exportar-excel/${idFactura}`, {
            method: 'GET',
            credentials: 'include', // IMPORTANTE: Envía la cookie de sesión (usuario logueado)
        });

        if (!response.ok) {
            throw new Error('Error al descargar el Excel');
        }

        // Convertimos la respuesta binaria a un Blob
        const blob = await response.blob();

        // Creamos una URL temporal
        const url = window.URL.createObjectURL(blob);

        // Creamos el link invisible para forzar la descarga
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `Factura_${idFactura}.xlsx`); // Extensión .xlsx

        document.body.appendChild(link);
        link.click();

        // Limpiamos
        link.parentNode.removeChild(link);
        window.URL.revokeObjectURL(url);

        return true; // Indicamos que terminó bien

    } catch (error) {
        console.error('Fallo la descarga del Excel:', error);
        alert('La factura se guardó, pero hubo un error al descargar el Excel.');
        return false;
    }
}