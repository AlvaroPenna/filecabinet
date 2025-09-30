// Función de ejemplo para manejar el envío del formulario
async function handleLogin(event) {
    // Detiene el envío real del formulario para manejarlo con JavaScript
    event.preventDefault(); 

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const messageBox = document.getElementById('messageBox');
    const loginButton = document.querySelector('.btn-submit');

    // 1. Validar campos
    if (username.trim() === '' || password.trim() === '') {
        messageBox.style.color = '#e74c3c';
        messageBox.innerHTML = 'Por favor, ingrese usuario y contraseña.';
        return;
    }

    // 2. Mostrar estado de carga y deshabilitar el botón
    loginButton.disabled = true;
    loginButton.textContent = 'Accediendo...';
    messageBox.innerHTML = ''; // Limpiar mensajes anteriores

    // 3. Preparar la URL y los datos para el backend
    // NOTA: Esta URL debe coincidir con tu endpoint de autenticación en Spring Security
    const loginUrl = '/api/auth/login'; 
    const credentials = {
        username: username, 
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
            // 4. Autenticación exitosa (código 200-299)
            messageBox.style.color = '#27ae60';
            messageBox.innerHTML = '¡Acceso exitoso! Redirigiendo...';
            
            // En una aplicación real, aquí guardarías el token JWT y redirigirías:
            // const data = await response.json(); 
            // localStorage.setItem('token', data.jwtToken);
            // window.location.href = '/dashboard';

        } else if (response.status === 401 || response.status === 403) {
            // 5. Manejo de credenciales inválidas
            messageBox.style.color = '#e74c3c';
            messageBox.innerHTML = 'Error de autenticación: Usuario o contraseña incorrectos.';

        } else {
            // 6. Manejo de otros errores del servidor (e.g., 500)
            messageBox.style.color = '#e74c3c';
            messageBox.innerHTML = `Error del servidor (${response.status}). Inténtelo de nuevo.`;
        }

    } catch (error) {
        // 7. Manejo de errores de red (e.g., el servidor no está disponible)
        console.error('Error durante el inicio de sesión:', error);
        messageBox.style.color = '#e74c3c';
        messageBox.innerHTML = 'Error de red. Verifique su conexión.';

    } finally {
        // 8. Restaurar el botón y la funcionalidad (después de 3 segundos o inmediatamente)
        setTimeout(() => {
             loginButton.disabled = false;
             loginButton.textContent = 'Acceder';
             // Limpiar el formulario solo si la autenticación fue exitosa
             if (messageBox.style.color === 'rgb(39, 174, 96)') { // 'rgb(39, 174, 96)' es #27ae60
                 document.getElementById('loginForm').reset();
             }
        }, 3000);
    }
}
