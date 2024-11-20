package org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.repositories.LocationRepository;
import org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.repositories.ProvinceRepository;
import org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.repositories.SupermarketRepository;
import org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.entities.Location;
import org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.entities.Province;
import org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.entities.Supermarket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/locations")
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private SupermarketRepository supermarketRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Lista todas las ubicaciones y las pasa como atributo al modelo para que sean accesibles en la vista `location.html`.
     *
     * @param model Objeto del modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para renderizar la lista de ubicaciones.
     */
    @GetMapping
    public String listLocations(Model model) {
        logger.info("Solicitando la lista de todas las ubicaciones...");
        List<Location> listLocations = null;
        try {
            listLocations = locationRepository.findAll();
            logger.info("Se han cargado {} ubicaciones.", listLocations.size());
        } catch (Exception e) {
            logger.error("Error al listar las ubicaciones: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al listar las ubicaciones.");
        }
        model.addAttribute("listLocations", listLocations);
        return "location";
    }

    /**
     * Muestra el formulario para crear una nueva ubicación.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva ubicación.");
        List<Province> listProvinces = null;
        List<Supermarket> listSupermarkets = null;
        try {
            listProvinces = provinceRepository.findAll();
            listSupermarkets = supermarketRepository.findAll();
        } catch (Exception e) {
            logger.error("Error al listar las provincias o supermercados: {}", e.getMessage());
        }
        model.addAttribute("location", new Location());
        model.addAttribute("listProvinces", listProvinces);
        model.addAttribute("listSupermarkets", listSupermarkets);
        return "location-form.html";
    }

    /**
     * Muestra el formulario para editar una ubicación existente.
     *
     * @param id    ID de la ubicación a editar.
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        logger.info("Mostrando formulario de edición para la ubicación con ID {}", id);
        Optional<Location> location = null;
        List<Province> listProvinces = null;
        List<Supermarket> listSupermarkets = null;
        try {
            location = locationRepository.findById(id);
            listProvinces = provinceRepository.findAll();
            listSupermarkets = supermarketRepository.findAll();
            if (location == null) {
                logger.warn("No se encontró la ubicación con ID {}", id);
            }
        } catch (Exception e) {
            logger.error("Error al obtener la ubicación con ID {}: {}", id, e.getMessage());
            model.addAttribute("errorMessage", "Error al obtener la ubicación.");
        }
        model.addAttribute("location", location.get());
        model.addAttribute("listProvinces", listProvinces);
        model.addAttribute("listSupermarkets", listSupermarkets);
        return "location-form.html";
    }

    /**
     * Inserta una nueva ubicación en la base de datos.
     *
     * @param location            Objeto que contiene los datos del formulario.
     * @param result              Resultado de la validación del formulario.
     * @param redirectAttributes  Atributos para mensajes flash de redirección.
     * @param locale              Localización para mensajes internacionalizados.
     * @param  model               Modelo para pasar datos a la vista.
     * @return Redirección a la lista de ubicaciones.
     */
    @PostMapping("/insert")
    public String insertLocation(@Valid @ModelAttribute("location") Location location, BindingResult result,
                                 RedirectAttributes redirectAttributes, Locale locale, Model model) {
        logger.info("Insertando nueva ubicación con dirección {}", location.getAddress());
        try {
            if (result.hasErrors()) {
                List<Province> listProvinces = provinceRepository.findAll();
                List<Supermarket> listSupermarkets = supermarketRepository.findAll();
                model.addAttribute("listProvinces", listProvinces);
                model.addAttribute("listSupermarkets", listSupermarkets);
                return "location-form.html";
            }
            if (locationRepository.existsByAddress(location.getAddress())) {
                logger.warn("La dirección de la ubicación {} ya existe.", location.getAddress());
                String errorMessage = messageSource.getMessage("msg.location-controller.insert.addressExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/locations/new";
            }
            locationRepository.save(location);
            logger.info("Ubicación {} insertada con éxito.", location.getAddress());
        } catch (Exception e) {
            logger.error("Error al insertar la ubicación {}: {}", location.getAddress(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.location-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/locations";
    }

    /**
     * Actualiza una ubicación existente en la base de datos.
     *
     * @param location            Objeto que contiene los datos del formulario.
     * @param result              Resultado de la validación del formulario.
     * @param redirectAttributes  Atributos para mensajes flash de redirección.
     * @param locale              Localización para mensajes internacionalizados.
     * @param  model              Modelo para pasar datos a la vista.
     * @return Redirección a la lista de ubicaciones.
     */
    @PostMapping("/update")
    public String updateLocation(@Valid @ModelAttribute("location") Location location, BindingResult result,
                                 RedirectAttributes redirectAttributes, Locale locale, Model model) {
        logger.info("Actualizando ubicación con ID {}", location.getId());
        try {
            if (result.hasErrors()) {
                List<Province> listProvinces = provinceRepository.findAll();
                List<Supermarket> listSupermarkets = supermarketRepository.findAll();
                model.addAttribute("listProvinces", listProvinces);
                model.addAttribute("listSupermarkets", listSupermarkets);
                return "location-form.html";
            }
            if (locationRepository.existsLocationByAddressAndNotId(location.getAddress(), location.getId())) {
                logger.warn("La dirección de la ubicación {} ya existe para otra ubicación.", location.getAddress());
                String errorMessage = messageSource.getMessage("msg.location-controller.update.addressExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/locations/edit?id=" + location.getId();
            }
            locationRepository.save(location);
            logger.info("Ubicación con ID {} actualizada con éxito.", location.getId());
        } catch (Exception e) {
            logger.error("Error al actualizar la ubicación con ID {}: {}", location.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.location-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/locations";
    }

    /**
     * Elimina una ubicación de la base de datos.
     *
     * @param id                  ID de la ubicación a eliminar.
     * @param redirectAttributes  Atributos para mensajes flash de redirección.
     * @return Redirección a la lista de ubicaciones.
     */
    @PostMapping("/delete")
    public String deleteLocation(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        logger.info("Eliminando ubicación con ID {}", id);
        try {
            locationRepository.deleteById(id);
            logger.info("Ubicación con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error al eliminar la ubicación con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la ubicación.");
        }
        return "redirect:/locations";
    }
}
