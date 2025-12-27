package com.filecabinet.filecabinet.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.filecabinet.filecabinet.enums.Rol;

import java.time.LocalDateTime; // Usar esto en lugar de Date
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter @Setter // IMPORTANTE: Getter y Setter separados
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    
    @Column(nullable = true)
    private String apellidos;
    
    private String telefono;
    private String email;
    private String domicilio;
    private String codigoPostal;
    private String provincia;
    private String poblacion;
    private String passwordHash;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro; // Moderno y seguro

    private boolean activo;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @ManyToMany
    @JoinTable(
        name = "rel_usuario_cliente",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "cliente_id")
    )
    // Inicializado para evitar NullPointer
    private Set<Cliente> clientes = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Empleado> trabajadores = new HashSet<>();

    // --- ESTABILIDAD DEL SET ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id != null && Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}