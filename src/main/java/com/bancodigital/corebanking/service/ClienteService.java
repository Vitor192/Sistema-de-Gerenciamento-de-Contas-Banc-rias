package com.bancodigital.corebanking.service;

import com.bancodigital.corebanking.exception.RecursoNaoEncontradoException;
import com.bancodigital.corebanking.model.Cliente;
import com.bancodigital.corebanking.model.ContaCorrente;
import com.bancodigital.corebanking.model.ContaPoupanca;
import com.bancodigital.corebanking.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ContaService contaService;

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Cliente buscarClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
    }

    public Cliente buscarClientePorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
    }

    @Transactional
    public Cliente cadastrarCliente(Cliente cliente) {
        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        Cliente cliente = buscarClientePorId(id);
        
        cliente.setNome(clienteAtualizado.getNome());
        cliente.setEmail(clienteAtualizado.getEmail());
        cliente.setTelefone(clienteAtualizado.getTelefone());
        cliente.setEndereco(clienteAtualizado.getEndereco());
        
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void excluirCliente(Long id) {
        Cliente cliente = buscarClientePorId(id);
        clienteRepository.delete(cliente);
    }
    
    @Transactional
    public ContaCorrente criarContaCorrenteParaCliente(Long clienteId) {
        Cliente cliente = buscarClientePorId(clienteId);
        return contaService.criarContaCorrente(cliente);
    }
    
    @Transactional
    public ContaPoupanca criarContaPoupancaParaCliente(Long clienteId) {
        Cliente cliente = buscarClientePorId(clienteId);
        return contaService.criarContaPoupanca(cliente);
    }
}