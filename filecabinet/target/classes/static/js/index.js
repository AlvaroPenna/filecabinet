// Función de ejemplo para manejar el envío del formulario
async function handleLogin(event) {
    // Detiene el envío real del formulario para manejarlo con JavaScript
    event.preventDefault(); 

    // OBTENER VALORES: Asumimos que el input es <input id="email">
    const email = document.getElementById('email').value; // CAMBIO CLAVE: Cambiado de 'username' a 'email'
    const password = document.getElementById('password').value;
    const messageBox = document.getElementById('messageBox');
    const loginButton = document.querySelector('.btn-submit');

    // 1. Validar campos
    if (email.trim() === '' || password.trim() === '') {
        messageBox.style.color = '#e74c3c';
        messageBox.innerHTML = 'Por favor, ingrese email y contraseña.';
        return;
    }

    // 2. Mostrar estado de carga y deshabilitar el botón
    loginButton.disabled = true;
    loginButton.textContent = 'Accediendo...';
    messageBox.innerHTML = ''; // Limpiar mensajes anteriores

    // 3. Preparar la URL y los datos para el backend
    // Ajustado a la ruta de tu controlador: @RequestMapping("/api/login")
    const loginUrl = '/api/login'; 
    
    // El objeto JSON debe coincidir con el DTO LoginRequest {email, password}
    const credentials = {
        email: email, // CAMBIO CLAVE: Enviamos 'email' en lugar de 'username'
        password: password
    };

    try {
        const response = await fetch(loginUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(credentials)
        });

        if (response.ok) { 
            // 4. Autenticación exitosa (código 200)
            messageBox.style.color = '#27ae60';
            messageBox.innerHTML = '¡Acceso exitoso! Redirigiendo...';
            
            // Aquí iría tu lógica de redirección
            // window.location.href = '/home';

        } else if (response.status === 401) {
            // 5. Manejo de credenciales inválidas (401 Unauthorized)
            messageBox.style.color = '#e74c3c';
            messageBox.innerHTML = 'Error de autenticación: Email o contraseña incorrectos.';

        } else {
            // 6. Manejo de otros errores del servidor (e.g., 500 Internal Server Error)
            messageBox.style.color = '#e74c3c';
            messageBox.innerHTML = `Error del servidor (${response.status}). Inténtelo de nuevo.`;
        }

    } catch (error) {
        // 7. Manejo de errores de red (e.g., el servidor no está disponible)
        console.error('Error durante el inicio de sesión:', error);
        messageBox.style.color = '#e74c3c';
        messageBox.innerHTML = 'Error de red. Verifique su conexión con el servidor.';

    } finally {
        // 8. Restaurar el botón y la funcionalidad
        setTimeout(() => {
             loginButton.disabled = false;
             loginButton.textContent = 'Acceder';
             // Limpiar el formulario solo si la autenticación fue exitosa
             if (messageBox.style.color === 'rgb(39, 174, 96)') { 
                 document.getElementById('loginForm').reset();
             }
        }, 1500); // Reducido el tiempo de espera a 1.5 segundos
    }
}