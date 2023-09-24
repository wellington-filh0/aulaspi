package ifrn.pi.eventos.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import ifrn.pi.eventos.models.Evento;
import ifrn.pi.eventos.repositories.EventoRepository;

@Controller

public class EventosController {

	@Autowired
	private EventoRepository er;

	@GetMapping("/eventos/form")
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

	@GetMapping("/submit")
	public ModelAndView listar() {
		List<Evento> eventos = er.findAll();
		ModelAndView mv = new ModelAndView("eventos/lista");
		mv.addObject("eventos", eventos);
		return mv;
	}

	@GetMapping("/submit/{id}")
	public ModelAndView detalhar(@PathVariable Long id) {
		ModelAndView md = new ModelAndView();
		Optional<Evento> opt = er.findById(id);
		if (opt.isEmpty()) {
			md.setViewName("redirect:/submit");
			return md;
		}

		md.setViewName("eventos/detalhes");
		Evento evento = opt.get();

		md.addObject("evento", evento);

		return md;
	}

}
