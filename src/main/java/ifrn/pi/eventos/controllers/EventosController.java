package ifrn.pi.eventos.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ifrn.pi.eventos.models.Convidado;
import ifrn.pi.eventos.models.Evento;
import ifrn.pi.eventos.repositories.ConvidadoRepository;
import ifrn.pi.eventos.repositories.EventoRepository;
import jakarta.validation.Valid;

@Controller

public class EventosController {

	@Autowired
	private EventoRepository er;
	@Autowired
	private ConvidadoRepository cr;

	@GetMapping("/eventos/form")
	public String form(Evento evento) {
		return "eventos/formEvento";
	}

	@PostMapping("/submit")
	public String salvar(@Valid Evento evento, BindingResult result, RedirectAttributes attributes) {
		
		if (result.hasErrors()) {
			return form(evento);
		}
		System.out.println("Dados do evento:");
		System.out.println("Nome: " + evento.getNome());
		System.out.println("Local: " + evento.getLocal());
		System.out.println("Data: " + evento.getData());
		System.out.println("Horário: " + evento.getHorario());

		er.save(evento);
		attributes.addFlashAttribute("mensagem", "Evento salvo com sucesso!");

		return "redirect:/submit";
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
	public ModelAndView detalhar(@PathVariable Long id, Convidado convidado) {
		ModelAndView md = new ModelAndView();
		Optional<Evento> opt = er.findById(id);
		if (opt.isEmpty()) {
			md.setViewName("redirect:/submit");
			return md;
		}

		md.setViewName("eventos/detalhes");
		Evento evento = opt.get();

		md.addObject("evento", evento);
		
		List<Convidado> convidados = cr.findByEvento(evento);
		md.addObject("convidados", convidados);

		return md;
	}
	
	@PostMapping("/submit/{idEvento}")
	public String salvarConvidado(@PathVariable Long idEvento, @Valid Convidado convidado, BindingResult resulta, RedirectAttributes attributes) {
		
		if (resulta.hasErrors()) {                      // CONCERTAR: NÃO ESTÁ APARECENDO A FRASE QUANDO DEIXA RG OU NOME VAZIO!!
			return "redirect:/submit/{idEvento}";
		}
		
		System.out.println("Id do evento: " + idEvento);
		System.out.println(convidado);
		
		Optional<Evento> opt = er.findById(idEvento);
		if(opt.isEmpty()) {
			return "redirect:/submit";
		}
		
		Evento evento = opt.get();
		convidado.setEvento(evento);
		
		cr.save(convidado);
		attributes.addFlashAttribute("mensagemConvidado", "Convidado salvo com sucesso!");
		
		return "redirect:/submit/{idEvento}";
	}
	
	@GetMapping("/eventos/{id}/selecionar")
	public ModelAndView selecionarEvento(@PathVariable Long id) {
		ModelAndView md = new ModelAndView();
		Optional<Evento> opt = er.findById(id);
		if(opt.isEmpty()) {
			md.setViewName("redirect:/eventos");
			return md;
		}
		
		Evento evento = opt.get();
		md.setViewName("eventos/formEvento");
		md.addObject("evento", evento);
		
		return md;
	}
	
	@GetMapping("/eventos/{idEvento}/convidados/{idConvidado}/selecionar")
	public ModelAndView selecionarConvidado(@PathVariable Long idEvento, @PathVariable Long idConvidado) {
		ModelAndView md = new ModelAndView();
		
		Optional<Evento> optEvento = er.findById(idEvento);
		Optional<Convidado> optConvidado = cr.findById(idConvidado);
		
		if(optEvento.isEmpty()|| optConvidado.isEmpty()) {
			md.setViewName("redirect:/eventos");
			return md;
		}
		
		Evento evento = optEvento.get();
		Convidado convidado = optConvidado.get();
		
		if(evento.getId() != convidado.getEvento().getId()) {
			md.setViewName("redirect:/eventos");
			return md;				
		}
		
		md.setViewName("eventos/detalhes");
		md.addObject("convidado", convidado);
		md.addObject("evento", evento);
		md.addObject("convidados", cr.findByEvento(evento));		
		return md;
	}
		
	@GetMapping("/eventos/{id}/remover")
	public String apagarEvento(@PathVariable Long id, RedirectAttributes attributes) {
		
		Optional<Evento> opt = er.findById(id);
		
		if(!opt.isEmpty()) {
			Evento evento = opt.get();
			
			List<Convidado> convidados = cr.findByEvento(evento);
			
			cr.deleteAll(convidados);			
			er.delete(evento);
			attributes.addFlashAttribute("mensagem", "Evento removido com sucesso!");
		}
		
		return "redirect:/submit";
	}
	
	@GetMapping("/eventos/{idEvento}/convidados/{idConvidado}/remover")
	public String apagarConvidado(@PathVariable Long idConvidado, RedirectAttributes attributes) {
		
		Optional<Convidado> optConvidado = cr.findById(idConvidado);
		
		if(!optConvidado.isEmpty()) {
			
			cr.deleteById(idConvidado);	
			attributes.addFlashAttribute("mensagemConvidado", "Convidado removido com sucesso!");
		}
		
		return "redirect:/submit/{idEvento}";
	}

}
