function volverAlIndex() {
        window.location.href = "/index"; 
}
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('cliente-form').addEventListener('submit', function(event) {
        event.preventDefault();

        const nombre = document.getElementById('nombre').value;
        const apellidos = document.getElementById('apellidos').value;
        const cifNif = document.getElementById('cifNif').value;
        const email = document.getElementById('email').value;
        const tel = document.getElementById('tel').value;
        const direccion = document.getElementById('direccion').value;
        const ciudad = document.getElementById('ciudad').value;
        const codigoPostal = document.getElementById('codigoPostal').value;

        const clienteData = {
            nombre: nombre,
            apellidos: apellidos,
            cifNif: cifNif,
            email: email,
            telefono: tel,
            direccion: direccion,
            ciudad: ciudad,
            codigoPostal: codigoPostal
        };
        
        fetch('http://localhost:8080/api/clientes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(clienteData)
        })
        .then(response => {
            if (response.ok) {
                alert('Cliente guardado con éxito! ✅');
                document.getElementById('cliente-form').reset();
            } else {
                alert('Error al guardar el cliente. Por favor, revisa los datos.');
                console.error('Error al guardar el cliente:', response.statusText);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Ocurrió un error al conectar con el servidor.');
        });
    });
});