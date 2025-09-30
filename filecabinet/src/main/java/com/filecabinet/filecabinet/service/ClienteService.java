package com.filecabinet.filecabinet.service;

import com.filecabinet.filecabinet.dto.ClienteDto;
import com.filecabinet.filecabinet.entidades.Cliente;
import com.filecabinet.filecabinet.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteDto> getAllClientes(){
        return clienteRepository.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ClienteDto> getClienteById(Long id){
        return clienteRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public ClienteDto createCliente(ClienteDto clienteDto) {
        Cliente cliente = toEntity(clienteDto);
        Cliente savedCliente = clienteRepository.save(cliente);
        return toDto(savedCliente);
    }

    @Transactional
    public Optional<ClienteDto> updateCliente(Long id, ClienteDto clienteDetails){
        return clienteRepository.findById(id).map(cliente -> {
            cliente.setId(clienteDetails.getId());
            cliente.setNombre(clienteDetails.getNombre());
            cliente.setApellidos(clienteDetails.getApellidos());
            cliente.setCifNif(clienteDetails.getCifNif());
            cliente.setDireccion(clienteDetails.getDireccion());
            cliente.setCiudad(clienteDetails.getCiudad());
            cliente.setCodigoPostal(cliente.getCodigoPostal());
            cliente.setEmail(clienteDetails.getEmail());
            cliente.setTelefono(clienteDetails.getTelefono());
            return toDto(clienteRepository.save(cliente));
        });
    }

    @Transactional
    public boolean deleteCliente(Long id){
        if(clienteRepository.existsById(id)){
            clienteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private Cliente toEntity(ClienteDto dto){
        if(dto == null){
            return null;
        }
        Cliente entity = new Cliente();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setApellidos(dto.getApellidos());
        entity.setCifNif(dto.getCifNif());
        entity.setDireccion(dto.getDireccion());
        entity.setCiudad(dto.getCiudad());
        entity.setCodigoPostal(dto.getCodigoPostal());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
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
        dto.setCifNif(cliente.getCifNif());
        dto.setDireccion(cliente.getDireccion());
        dto.setCiudad(cliente.getCiudad());
        dto.setCodigoPostal(cliente.getCodigoPostal());
        dto.setEmail(cliente.getEmail());
        dto.setTelefono(cliente.getTelefono());
        return dto;
    }
}
