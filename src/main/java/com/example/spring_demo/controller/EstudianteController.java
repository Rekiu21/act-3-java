package com.example.spring_demo.controller;

import com.example.spring_demo.model.Estudiante;
import com.example.spring_demo.repository.EstudianteRepository;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estudiantes")
public class EstudianteController {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @GetMapping
    public String listar(@RequestParam(required = false, defaultValue = "") String q, Model model) {
        List<Estudiante> estudiantes = q.isBlank()
                ? estudianteRepository.findAll()
                : estudianteRepository.findByNombreContainingIgnoreCase(q);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("q", q);
        return "estudiantes/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        Estudiante estudiante = new Estudiante();
        estudiante.setFechaRegistro(LocalDate.now());
        model.addAttribute("estudiante", estudiante);
        return "estudiantes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Estudiante estudiante, BindingResult result, Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("estudiante", estudiante);
            return "estudiantes/formulario";
        }

        estudianteRepository.save(estudiante);
        redirectAttributes.addFlashAttribute("mensaje", "Estudiante guardado correctamente");
        return "redirect:/estudiantes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return estudianteRepository.findById(id)
                .map(estudiante -> {
                    model.addAttribute("estudiante", estudiante);
                    return "estudiantes/formulario";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Estudiante no encontrado");
                    return "redirect:/estudiantes";
                });
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute Estudiante estudiante, BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            estudiante.setId(id);
            model.addAttribute("estudiante", estudiante);
            return "estudiantes/formulario";
        }

        estudiante.setId(id);
        estudianteRepository.save(estudiante);
        redirectAttributes.addFlashAttribute("mensaje", "Estudiante actualizado correctamente");
        return "redirect:/estudiantes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (estudianteRepository.existsById(id)) {
            estudianteRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Estudiante eliminado");
        } else {
            redirectAttributes.addFlashAttribute("error", "Estudiante no encontrado");
        }
        return "redirect:/estudiantes";
    }
}
