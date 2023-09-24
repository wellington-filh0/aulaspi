package ifrn.pi.eventos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ifrn.pi.eventos.models.Evento;
import ifrn.pi.eventos.repositories.EventoRepository;

@Controller

public class EventosController {
	
	@Autowired
	private EventoRepository er;

	@RequestMapping("/eventos/form")
	public String form() {
		return "eventos/formEvento";
	}

	@PostMapping("/submit")
	public String submetido(Evento evento) {
		System.out.println("Dados do evento:");
		System.out.println("Nome: " + evento.getNome());
		System.out.println("Local: " + evento.getLocal());
		System.out.println("Data: " + evento.getData());
		System.out.println("Horário: " + evento.getHorario());
		
		er.save(evento);

		return "redirect:/success";
	}
	
	@GetMapping("/success")
	public String successPage() {
		return "eventos/success";
	}

}
