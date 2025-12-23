document.addEventListener('DOMContentLoaded', () => {

    cargarTrabajadores();
    cargarProyectos();

    const form = document.getElementById('horas-form');
    const container = form;

    const templateContainer = document.createElement('div');
    templateContainer.className = 'form-rows';

    const formGroups = [
        document.querySelector('.form-group #trabajador').closest('.form-group'),
        document.querySelector('.form-group #obra').closest('.form-group'),
        document.querySelector('.form-group #dia').closest('.form-group'),
        document.querySelector('.form-group #horas').closest('.form-group'),
        document.querySelector('.form-group #descripcion').closest('.form-group')
    ];

    formGroups.forEach(group => {
        if (group) {
            templateContainer.appendChild(group.cloneNode(true));
        }
    });

    // 1. Clonar y limpiar el primer grupo de campos
    function cloneAndResetFields(groupContainer) {
        const clone = groupContainer.cloneNode(true);
        const newRowIndex = document.querySelectorAll('.form-rows').length + 1;

        // CORRECCIÓN: Referencia a los selects originales para copiar sus opciones
        const originalSelects = document.querySelector('.form-rows').querySelectorAll('select');
        const cloneSelects = clone.querySelectorAll('select');

        clone.querySelectorAll('select, input, textarea').forEach((field, index) => {
            const originalId = field.id;
            field.id = `${originalId}-${newRowIndex}`;
            field.name = `${originalId}-${newRowIndex}`;

            if (field.tagName === 'SELECT') {
                // CORRECCIÓN: Copiar el contenido (options) del select original al clonado
                field.innerHTML = originalSelects[index].innerHTML;
                field.selectedIndex = 0;
            } else {
                field.value = '';
            }

            const label = clone.querySelector(`label[for="${originalId}"]`);
            if (label) {
                label.setAttribute('for', field.id);
            }
        });

        return clone;
    }

    // 2. Función para añadir una nueva fila al formulario
    function addFormRow() {
        const newRow = cloneAndResetFields(templateContainer);

        const deleteButtonDiv = document.createElement('div');
        deleteButtonDiv.className = 'form-group form-actions-row';
        deleteButtonDiv.innerHTML = `
            <button type="button" class="btn-delete-row">X</button>
        `;

        form.insertBefore(newRow, document.querySelector('.form-actions'));
        newRow.appendChild(deleteButtonDiv);

        deleteButtonDiv.querySelector('.btn-delete-row').addEventListener('click', function () {
            newRow.remove();
        });
    }

    form.addEventListener('submit', async function (event) {
        event.preventDefault();
        const filas = document.querySelectorAll('.form-rows');
        const promesas = [];

        filas.forEach(fila => {
            const trabajador = fila.querySelector('select[name^="trabajador"]').value;
            const proyecto = fila.querySelector('select[name^="obra"]').value;
            const fecha = fila.querySelector('input[name^="dia"]').value;
            const horas = fila.querySelector('input[name^="horas"]').value;
            const descripcion = fila.querySelector('textarea[name^="descripcion"]').value;

            if (trabajador && proyecto && fecha && horas) {
                promesas.push(
                    fetch('http://localhost:8080/api/proyectos-trabajadores', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            empleado_id: parseInt(trabajador),
                            proyecto_id: parseInt(proyecto),
                            dia: fecha,
                            horas: parseFloat(horas),
                            tareaRealizada: descripcion
                        })
                    })
                );
            }
        });

        try {
            await Promise.all(promesas);
            alert('¡Guardado con éxito! ✅');
            window.location.reload();
        } catch (error) {
            alert('Error al guardar algunas filas.');
        }
    });

    // 4. Iniciar: crear el contenedor de la primera fila y el botón "+"
    const firstRow = document.createElement('div');
    firstRow.className = 'form-rows';
    formGroups.forEach(group => {
        firstRow.appendChild(group);
    });
    form.insertBefore(firstRow, document.querySelector('.form-actions'));

    const addButtonDiv = document.createElement('div');
    addButtonDiv.className = 'form-group form-actions-row';
    addButtonDiv.innerHTML = `
        <button type="button" class="btn-add-row">+</button>
    `;
    firstRow.appendChild(addButtonDiv);

    addButtonDiv.querySelector('.btn-add-row').addEventListener('click', addFormRow);
});

function volverAlIndex() {
    window.location.href = "/index";
}

function cargarTrabajadores() {
    fetch('http://localhost:8080/api/trabajadores')
        .then(response => {
            if (!response.ok) throw new Error('Error al obtener trabajadores');
            return response.json();
        })
        .then(trabajadores => {
            const selectTrabajador = document.getElementById('trabajador');
            selectTrabajador.innerHTML = '<option value="">Selecciona un trabajador...</option>';
            trabajadores.forEach(trabajador => {
                const option = document.createElement('option');
                option.value = trabajador.id;
                option.textContent = trabajador.nif + " - " + trabajador.nombre + " " + (trabajador.apellidos || "");
                selectTrabajador.appendChild(option);
            });
        })
        .catch(error => console.error('Error al cargar trabajadores:', error));
}

function cargarProyectos() {
    fetch('http://localhost:8080/api/proyectos')
        .then(response => {
            if (!response.ok) throw new Error('Error al obtener proyectos');
            return response.json();
        })
        .then(proyectos => {
            const selectProyecto = document.getElementById('obra');
            selectProyecto.innerHTML = '<option value="">Selecciona un proyecto...</option>';
            proyectos.forEach(proyecto => {
                const option = document.createElement('option');
                option.value = proyecto.id;
                option.textContent = proyecto.nombre;
                selectProyecto.appendChild(option);
            });
        })
        .catch(error => console.error('Error al cargar proyectos:', error));
}