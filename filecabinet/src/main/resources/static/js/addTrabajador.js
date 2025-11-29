function volverAlIndex() {
        window.location.href = "/index"; 
}
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('trabajador-form').addEventListener('submit', function(event) {
        event.preventDefault();

        const nombre = document.getElementById('nombre').value;
        const apellidos = document.getElementById('apellidos').value;
        const nif = document.getElementById('nif').value;
        const email = document.getElementById('email').value;
        const tel = document.getElementById('tel').value;
        const direccion = document.getElementById('direccion').value;
        const ciudad = document.getElementById('ciudad').value;
        const codigoPostal = document.getElementById('codigoPostal').value;

        const trabajadorData = {
            nombre: nombre,
            apellidos: apellidos,
            nif: nif,
            telefono: tel,
            email: email,
            direccion: direccion,
            ciudad: ciudad,
            codigoPostal: codigoPostal
        };
        
        fetch('http://localhost:8080/api/trabajadores', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(trabajadorData)
        })
        .then(response => {
            if (response.ok) {
                alert('Trabajador guardado con éxito! ✅');
                document.getElementById('trabajador-form').reset();
            } else {
                alert('Error al guardar el trabajador. Por favor, revisa los datos.');
                console.error('Error al guardar el trabajador:', response.statusText);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Ocurrió un error al conectar con el servidor.');
        });
    });
});