package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.modelo.AdicionadorLinkTelefone;
import com.autobots.automanager.modelo.Selecionador;
import com.autobots.automanager.modelo.TelefoneAtualizador;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/telefone")
public class TelefoneControle {
	@Autowired
	private TelefoneRepositorio repositorio;
	@Autowired
	private ClienteRepositorio ClienteRepositorio;
	@Autowired
	private AdicionadorLinkTelefone AdicionadorLink;

	@GetMapping("/telefone/{id}")
	public ResponseEntity<Telefone> obterTelefone(@PathVariable long id) {
		List<Telefone> telefones = repositorio.findAll();
		Telefone telefone = Selecionador.telefoneSelecionador(telefones, id);
		if (telefone == null) {
			ResponseEntity<Telefone> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			AdicionadorLink.adicionarLink(telefone);
			ResponseEntity<Telefone> resposta = new ResponseEntity<Telefone>(telefone, HttpStatus.FOUND);
			return resposta;
		}
	}

	@GetMapping("/telefones")
	public ResponseEntity<List<Telefone>> obterTelefones() {
		List<Telefone> telefones = repositorio.findAll();
		if (telefones.isEmpty()) {
			ResponseEntity<List<Telefone>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			AdicionadorLink.adicionarLink(telefones);
			ResponseEntity<List<Telefone>> resposta = new ResponseEntity<>(telefones, HttpStatus.FOUND);
			return resposta;
		}
	}

	@PostMapping("/cadastro/{id}")
	public ResponseEntity<?> cadastrarTelefone(@RequestBody Telefone telefone, @PathVariable long id) {
		HttpStatus status = HttpStatus.CONFLICT;
		if (telefone == null) {
			Cliente cliente = ClienteRepositorio.getById(id);
			List<Telefone> telefones = cliente.getTelefones();
			telefones.add(telefone);
			cliente.setTelefones(telefones);
			ClienteRepositorio.save(cliente);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<>(status);
	}

	@PutMapping("/atualizar")
	public ResponseEntity<?> atualizarTelefone(@RequestBody Telefone atualizacao) {
		HttpStatus status = HttpStatus.CONFLICT;
		Telefone telefone = repositorio.getById(atualizacao.getId());
		if (telefone != null) {
			TelefoneAtualizador atualizador = new TelefoneAtualizador();
			atualizador.atualizar(telefone, atualizacao);
			repositorio.save(telefone);
			status = HttpStatus.OK;
		} else {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(status);
	}

	@DeleteMapping("/excluir/{id}")
	public ResponseEntity<?> excluirTelefone(@RequestBody Telefone exclusao, @PathVariable long id) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		Telefone telefone = repositorio.getById(exclusao.getId());
		if (telefone != null) {
			Cliente cliente = ClienteRepositorio.getById(id);
			List<Telefone> telefones = cliente.getTelefones();
			for (int i=0; i<telefones.size(); i++) {
				if (telefones.get(i).getId() == exclusao.getId()) {
					telefones.remove(i);
					break;
				}
			}
			cliente.setTelefones(telefones);
			ClienteRepositorio.save(cliente);
			status = HttpStatus.OK;
		}
		return new ResponseEntity<>(status);
	}
}