document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    } else {
        console.error("No se encontró el formulario con id 'loginForm'.");
    }
});

async function handleLogin(event) {
    // 1. Detiene el envío real del formulario para manejarlo con JavaScript
    event.preventDefault(); 
    // 2. OBTENER VALORES
    const email = document.getElementById('email').value; 
    const password = document.getElementById('password').value;
    const messageBox = document.getElementById('messageBox');
    const loginButton = document.querySelector('.btn-submit');

    // 3. Validar campos
    if (email.trim() === '' || password.trim() === '') {
        messageBox.style.color = '#e74c3c';
        messageBox.innerHTML = 'Por favor, ingrese email y contraseña.';
        return;
    }

    // 4. Mostrar estado de carga y deshabilitar el botón
    loginButton.disabled = true;
    loginButton.textContent = 'Accediendo...';
    messageBox.innerHTML = ''; // Limpiar mensajes anteriores

    // 5. Preparar la URL y los datos para el backend
    const loginUrl = '/perform_login'; 
    const formData = new URLSearchParams();
    formData.append('username', email);
    formData.append('password', password);

    let loginFailed = false;
    let response;
    
    try {
        // 6. Enviar la solicitud
        response = await fetch(loginUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData 
        });

        // 7. Analizar la respuesta de Spring Security
        
        // ÉXITO
        if (response.redirected && response.url.includes('/index')) {
            messageBox.style.color = '#27ae60';
            messageBox.innerHTML = '¡Acceso exitoso! Redirigiendo...';
            window.location.href = '/index'; 
            
        // FALLO (Redirección a /login?error)
        } else if (response.redirected && response.url.includes('/login?error')) {
            messageBox.style.color = '#e74c3c';
            messageBox.innerHTML = 'Error de autenticación: Email o contraseña incorrectos.';
            loginFailed = true;
            
        // Error Inesperado del servidor
        } else {
            messageBox.style.color = '#e74c3c';
            messageBox.innerHTML = `Respuesta inesperada del servidor. (Consola)`;
            console.log("Respuesta de login inesperada:", response);
            loginFailed = true;
        }

    } catch (error) {
        // 8. Manejo de errores de red
        console.error('Error durante el inicio de sesión:', error);
        messageBox.style.color = '#e74c3c';
        messageBox.innerHTML = 'Error de red. Verifique su conexión con el servidor.';
        loginFailed = true; // Error de red también marca el fallo
        
    } finally {
        if (loginFailed) {
            setTimeout(() => {
                window.location.reload(); 
            }, 1500);
        } 
    }
}