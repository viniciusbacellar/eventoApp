package com.eventoapp.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventoapp.model.Convidado;
import com.eventoapp.model.Evento;
import com.eventoapp.repository.ConvidadoRepository;
import com.eventoapp.repository.EventoRepository;

@Controller
public class EventoController {

	@Autowired
	private EventoRepository er;

	@Autowired
	private ConvidadoRepository cr;

	@RequestMapping(value = "/cadastrarEvento", method = RequestMethod.GET)
	public String form() {
		return "evento/formEvento";
	}

	@RequestMapping(value = "/cadastrarEvento", method = RequestMethod.POST)
	public String form(@Valid Evento evento, BindingResult result, RedirectAttributes attributes) {
		if(result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos!");
			return "redirect:/cadastrarEvento";
		}
		er.save(evento);
		return "redirect:/eventos";
	}

	@RequestMapping("/eventos")
	public ModelAndView listaEventos() {
		ModelAndView mv = new ModelAndView("index");
		Iterable<Evento> eventos = er.findAll();
		mv.addObject("eventos", eventos);
		return mv;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView detalhesEventos(@PathVariable("id") long id) {
		Evento evento = er.findById(id);
		ModelAndView mv = new ModelAndView("evento/detalhesEvento");
		mv.addObject("evento", evento);
		List<Convidado> convidados = cr.findAll();
		mv.addObject("convidados", convidados);
		return mv;
	}
	
	@RequestMapping("/deletarEvento")
	public String deletarEvento(long id) {
		Evento evento = er.findById(id);
		er.delete(evento);
		return "redirect:/eventos";
	}
	

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String detalhesEventoPost(@PathVariable("id") long id, @Valid Convidado convidado, BindingResult result,
			RedirectAttributes attributes) {
		if (result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos!");
			return "redirect:/{id}";
		}
		Evento evento = er.findById(id);
		convidado.setEvento(evento);
		cr.save(convidado);
		attributes.addFlashAttribute("mensagem", "Convidado adicionado com sucesso!");
		return "redirect:/{id}";
	}
	
	@RequestMapping("/deletarConvidado")
	public String deletarConvidado(String rg) {
		Convidado convidado = cr.findByRg(rg);
		cr.delete(convidado);
		Evento evento = convidado.getEvento();
		long codigoLong = evento.getId();
		String id = "" + codigoLong;
		return "redirect:/" + id;
	}
}
