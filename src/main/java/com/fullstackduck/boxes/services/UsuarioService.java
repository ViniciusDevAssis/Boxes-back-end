package com.fullstackduck.boxes.services;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fullstackduck.boxes.entities.Usuario;
import com.fullstackduck.boxes.entities.enums.TipoLicenca;
import com.fullstackduck.boxes.repositories.UsuarioRepository;
import com.fullstackduck.boxes.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service //Registro de componente
public class UsuarioService implements UserDetailsService {
	
	private static Logger logger = LoggerFactory.getLogger(UsuarioService.class);

	@Autowired
	private UsuarioRepository repository;
	
	public List<Usuario> findAll(){
		return repository.findAll();
	}
	
	public Usuario findById(Long id) {
		Optional<Usuario> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ResourceNotFoundException(id));
	}
	
	//insere usuario no banco de dados
	public Usuario inserirUsuario(Usuario obj) {
		obj.setDatacadastro(Instant.now());
	    /*
	    // Definir tipo de licença escolhido pelo usuário
	    TipoLicenca tipoLicenca = obj.getTipoLicenca();
	    obj.setTipoLicenca(tipoLicenca);
	    
	    // Definir data de validade da licença baseado no tipo escolhido
	    Instant dataValidadeLicenca = Instant.now();
	    if (tipoLicenca == TipoLicenca.GRATUITA) {
	        dataValidadeLicenca = dataValidadeLicenca.plus(Duration.ofDays(15));
	    } else if (tipoLicenca == TipoLicenca.MENSAL) {
	        dataValidadeLicenca = dataValidadeLicenca.plus(Duration.ofDays(30));
	    } else if (tipoLicenca == TipoLicenca.SEMESTRAL) {
	        dataValidadeLicenca = dataValidadeLicenca.plus(Duration.ofDays(180));
	    } else if (tipoLicenca == TipoLicenca.ANUAL) {
	        dataValidadeLicenca = dataValidadeLicenca.plus(Duration.ofDays(365));
	    }
	    obj.setDataValidadeLicenca(dataValidadeLicenca);
	    */
	    return repository.save(obj);
	}
	
	//atualiza status do usuario no banco de dados
	public Usuario atualizarStatusUsuario(Long id, Usuario obj) {
		try {
			Usuario entity = repository.getReferenceById(id);
			atualizarStatus(entity, obj);
			return repository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}
	}
	
	//atualiza dados do usuario no banco de dados
	public Usuario atualizarUsuario(Long id, Usuario obj) {
		try {
			Usuario entity = repository.getReferenceById(id);
			atualizarDados(entity, obj);
			return repository.save(entity);
		}catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}
	}
	
	private void atualizarDados(Usuario entity, Usuario obj) {
		entity.setNome(obj.getNome());
		entity.setDocumento(obj.getDocumento());
		entity.setEmail(obj.getEmail());
		entity.setTelefone(obj.getTelefone());
		entity.setSenha(obj.getSenha());
		entity.setEndereco(obj.getEndereco());
		entity.setLogo(obj.getLogo());
	}
	
	private void atualizarStatus(Usuario entity, Usuario obj) {
		entity.setStatus(obj.getStatus());
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
		Usuario usuario = repository.findByEmail(username);
		if (usuario == null) {
			logger.error("Usuario não encontrado: " + username);
			throw new UsernameNotFoundException("Email not found");
		}
		logger.info("Usuario encontrado: " + username);
		return usuario;
		
	}
	
	

}

