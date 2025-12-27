package com.filecabinet.filecabinet.service;

import com.filecabinet.filecabinet.dto.ClienteDto;
import com.filecabinet.filecabinet.entidades.Cliente;
import com.filecabinet.filecabinet.entidades.Usuario;
import com.filecabinet.filecabinet.repository.ClienteRepository;
import com.filecabinet.filecabinet.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public ClienteService(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }
/** 
    @Transactional(readOnly = true)
    public List<ClienteDto> getAllClientes() {
        return clienteRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ClienteDto> getClienteById(Long id) {
        return clienteRepository.findById(id).map(this::toDto);
    }
*/

@Transactional
public ClienteDto createCliente(ClienteDto clienteDto, Long userId) {

    // 1. Recuperar Usuario (Necesario para la relación)
    Usuario usuario = usuarioRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // 2. Buscar si el cliente YA existe (por CIF o Email)
    // Asumiendo que tus repos devuelven Optional, usamos .orElse(null) para facilitar la lógica
    Cliente clienteParaVincular = clienteRepository.findByCif(clienteDto.getCif())
            .orElse(clienteRepository.findByEmail(clienteDto.getEmail())
            .orElse(null));

    // 3. Lógica de "Crear o Reutilizar"
    if (clienteParaVincular == null) {
        // NO existe -> Lo creamos y guardamos primero
        clienteParaVincular = toEntity(clienteDto);
        clienteParaVincular = clienteRepository.save(clienteParaVincular);
    } 
    // Si ya existía (else), simplemente usamos la variable 'clienteParaVincular' que ya recuperamos

    // 4. CREAR LA RELACIÓN (Lo que faltaba en tu código)
    // Esto inserta la fila en 'rel_cliente_usuario'
    boolean nuevaRelacion = usuario.getClientes().add(clienteParaVincular);
    
    // Opcional: Validar si ya lo tenía asignado
    if (!nuevaRelacion) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Este cliente ya está asociado a tu usuario.");
    }

    usuarioRepository.save(usuario); // Guarda la relación

    return toDto(clienteParaVincular);
}

    @Transactional
    public Optional<ClienteDto> updateCliente(Long id, ClienteDto clienteDto, Long userId) {
        return clienteRepository.findByIdAndUsuarios_Id(id, userId).map(clienteExistente -> {
            clienteExistente.setNombre(clienteDto.getNombre());
            clienteExistente.setApellidos(clienteDto.getApellidos());
            clienteExistente.setCif(clienteDto.getCif());
            clienteExistente.setDireccion(clienteDto.getDireccion());
            clienteExistente.setCiudad(clienteDto.getCiudad());
            clienteExistente.setCodigoPostal(clienteDto.getCodigoPostal());
            clienteExistente.setEmail(clienteDto.getEmail());
            clienteExistente.setTelefono(clienteDto.getTelefono());
            Cliente guardado = clienteRepository.save(clienteExistente);
            return toDto(guardado);
        });
    }

    @Transactional
    public boolean deleteCliente(Long id, Long userId) {
        Usuario usuario = usuarioRepository.findById(userId).orElse(null);
        if (usuario == null)
            return false;

        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null)
            return false;

        boolean estabaPresente = usuario.getClientes().remove(cliente);

        if (estabaPresente) {
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

    private Cliente toEntity(ClienteDto dto) {
        if (dto == null) {
            return null;
        }
        Cliente entity = new Cliente();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setApellidos(dto.getApellidos());
        entity.setCif(dto.getCif());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        entity.setDireccion(dto.getDireccion());
        entity.setCiudad(dto.getCiudad());
        entity.setCodigoPostal(dto.getCodigoPostal());
        return entity;
    }

    private ClienteDto toDto(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        ClienteDto dto = new ClienteDto();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellidos(cliente.getApellidos());
        dto.setCif(cliente.getCif());
        dto.setDireccion(cliente.getDireccion());
        dto.setCiudad(cliente.getCiudad());
        dto.setCodigoPostal(cliente.getCodigoPostal());
        dto.setEmail(cliente.getEmail());
        dto.setTelefono(cliente.getTelefono());
        return dto;
    }

    @Transactional(readOnly = true)
    public Cliente clienteById(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    public List<ClienteDto> getClientesByUsuarioId(Long userId) {

        List<Cliente> clientes = clienteRepository.findByUsuarios_Id(userId);
        return clientes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ClienteDto> getClienteByIdAndUsuario(Long clienteId, Long userId) {
        return clienteRepository.findByIdAndUsuarios_Id(clienteId, userId)
                .map(this::toDto);
    }

}
