   package com.fullstackduck.boxes.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fullstackduck.boxes.entities.ItensOrcamento;
import com.fullstackduck.boxes.entities.Orcamento;
import com.fullstackduck.boxes.entities.Produto;
import com.fullstackduck.boxes.entities.pk.ItensOrcamentoPK;
import com.fullstackduck.boxes.repositories.ItensOrcamentoRepository;
import com.fullstackduck.boxes.repositories.OrcamentoRepository;
import com.fullstackduck.boxes.repositories.ProdutoRepository;
import com.fullstackduck.boxes.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service //Registro de componente
public class OrcamentoService {
	
	 private static final double DESCONTO_PADRAO = 0.1;

	@Autowired
	private OrcamentoRepository orcamentoRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private ItensOrcamentoRepository itensRepository;
	
	public List<Orcamento> findAll(){
		return orcamentoRepository.findAll();
	}
	
	public Orcamento findById(Long id) {
		Optional<Orcamento> obj = orcamentoRepository.findById(id);
		return obj.get();
	}

	//insere orcamento no banco de dados
	public Orcamento inserirOrcamento(Orcamento obj) {
		return orcamentoRepository.save(obj);
	}
	
	//atualiza status do orcamento no banco de dados
	public Orcamento atualizarStatusOrcamento(Long id, Orcamento obj) {
		try {
			Orcamento entity = orcamentoRepository.getReferenceById(id);
			atualizarStatus(entity, obj);
			return orcamentoRepository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}
	}
	
	//atualiza dados do orcamento no banco de dados
	public Orcamento atualizarOrcamento(Long id, Orcamento obj) {
		try {
			Orcamento entity = orcamentoRepository.getReferenceById(id);
			atualizarDados(entity, obj);
			return orcamentoRepository.save(entity);
		}catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}
	}
	
	public Orcamento adicionarItem(Long orcamentoId, Integer produtoId, Integer quantidade) {
	    Orcamento orcamento = orcamentoRepository.getReferenceById(orcamentoId);
	    Produto produto = produtoRepository.getReferenceById(produtoId);
	    ItensOrcamento item = new ItensOrcamento();
	    item.setProduto(produto);
	    item.setPrecoUnit(produto.getValor());
	    item.setQuantidade(quantidade);
	    item.setOrcamento(orcamento);
	    item.setPrecoTotal(produto.getValor());
	    orcamento.adicionarItem(item);
	    itensRepository.save(item);
	    return orcamentoRepository.save(orcamento);
	  }
	
	public Orcamento removerItem(Long orcamentoId, Long produtoId) {
	    Orcamento orcamento = orcamentoRepository.findById(orcamentoId).orElseThrow(() -> new ResourceNotFoundException("Orcamento não encontrado com o id: " + orcamentoId));
	    Produto produto = produtoRepository.findById(produtoId).orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o id: " + produtoId));
	    ItensOrcamentoPK id = new ItensOrcamentoPK(orcamento, produto);
	    ItensOrcamento item = itensRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ItensOrcamento não encontrado para o orcamento com id: " + orcamentoId + " e produto com id: " + produtoId));
	    orcamento.getItens().remove(item);
	    return orcamentoRepository.save(orcamento);
	}

	
	private void atualizarDados(Orcamento entity, Orcamento obj) {
		entity.setTipoEntrega(obj.getTipoEntrega());
	}
	
	private void atualizarStatus(Orcamento entity, Orcamento obj) {
		entity.setStatus(obj.getStatus());
	}
    
    public void calcularTotal(Orcamento obj) {
    	Double total = 0.0;
		for(ItensOrcamento i: obj.getItens()) {
			total = total + i.getPrecoTotal();
			obj.setTotal(total);
		}
		orcamentoRepository.save(obj);
	}
}
