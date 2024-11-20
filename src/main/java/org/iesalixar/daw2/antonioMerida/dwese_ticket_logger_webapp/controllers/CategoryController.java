package org.iesalixar.daw2.antonioMerida.dwese_ticket_logger_webapp.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.antonioMerida.dwese_ticket_logger_webapp.repositories.CategoryRepository;
import org.iesalixar.daw2.antonioMerida.dwese_ticket_logger_webapp.entities.Category;
import org.iesalixar.daw2.antonioMerida.dwese_ticket_logger_webapp.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Lista todas las categorías y las pasa como atributo al modelo para que sean accesibles en la vista `category.html`.
     *
     * @param model Objeto del modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para renderizar la lista de categorías.
     */
    @GetMapping
    public String listCategories(Model model) {
        logger.info("Solicitando la lista de todas las categorías...");
        List<Category> listCategories = categoryRepository.findAll();
        logger.info("Se han cargado {} categorías.", listCategories.size());
        model.addAttribute("listCategories", listCategories);
        return "category";
    }

    /**
     * Muestra el formulario para crear una nueva categoría.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva categoría.");
        List<Category> parentCategories = categoryRepository.findAll();
        model.addAttribute("category", new Category());
        model.addAttribute("parentCategories", parentCategories);
        return "category-form";
    }

    /**
     * Muestra el formulario para editar una categoría existente.
     *
     * @param id    ID de la categoría a editar.
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        logger.info("Mostrando formulario de edición para la categoría con ID {}", id);
        Optional<Category> category = categoryRepository.findById(id);
        List<Category> parentCategories = categoryRepository.findAll();
        model.addAttribute("category", category.get());
        model.addAttribute("parentCategories", parentCategories);
        return "category-form";
    }

    /**
     * Inserta una nueva categoría en la base de datos.
     *
     * @param category            Objeto que contiene los datos del formulario.
     * @param result              Resultado de la validación del formulario.
     * @param redirectAttributes  Atributos para mensajes flash de redirección.
     * @param locale              Localización para mensajes internacionalizados.
     * @param model               Modelo para pasar datos a la vista.
     * @return Redirección a la lista de categorías.
     */
    @PostMapping("/insert")
    public String insertCategory(@Valid @ModelAttribute("category") Category category, BindingResult result,
                                 @RequestParam("imageFile") MultipartFile imageFile,
                                 RedirectAttributes redirectAttributes, Locale locale, Model model) {
        logger.info("Insertando nueva categoría con nombre {}", category.getName());
        if (result.hasErrors()) {
            List<Category> parentCategories = categoryRepository.findAll();
            model.addAttribute("parentCategories", parentCategories);
            return "category-form";
        }

        if (categoryRepository.existsByName(category.getName())) {
            logger.warn("El nombre de la categoría {} ya existe.", category.getName());
            String errorMessage = messageSource.getMessage("msg.category-controller.insert.nameExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/categories/new";
        }

        // Verificar si parentCategory está vacío o tiene un ID válido
        if (category.getParentCategory() != null && (category.getParentCategory().getId() == null || category.getParentCategory().getId() == 0)) {
            category.setParentCategory(null); // Si no tiene un ID válido, asignarlo a null
        }

        // Guardar la imagen subida
        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile);
            if (fileName != null) {
                category.setImage(fileName); // Guardar el nombre del archivo en la entidad
            }
        }

        categoryRepository.save(category);
        logger.info("Categoría {} insertada con éxito.", category.getName());
        return "redirect:/categories";
    }

    /**
     * Actualiza una categoría existente en la base de datos.
     *
     * @param category            Objeto que contiene los datos del formulario.
     * @param result              Resultado de la validación del formulario.
     * @param redirectAttributes  Atributos para mensajes flash de redirección.
     * @param locale              Localización para mensajes internacionalizados.
     * @param model               Modelo para pasar datos a la vista.
     * @return Redirección a la lista de categorías.
     */
    @PostMapping("/update")
    public String updateCategory(@Valid @ModelAttribute("category") Category category, BindingResult result,
                                 @RequestParam("imageFile") MultipartFile imageFile,
                                 RedirectAttributes redirectAttributes, Locale locale, Model model) {
        logger.info("Actualizando categoría con ID {}", category.getId());
        if (result.hasErrors()) {
            List<Category> parentCategories = categoryRepository.findAll();
            model.addAttribute("parentCategories", parentCategories);
            return "category-form";
        }

        if (categoryRepository.existsCategoryByNameAndNotId(category.getName(), category.getId())) {
            logger.warn("El nombre de la categoría {} ya existe para otra categoría.", category.getName());
            String errorMessage = messageSource.getMessage("msg.category-controller.update.nameExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/categories/edit?id=" + category.getId();
        }

        // Verificar si parentCategory está vacío o tiene un ID válido
        if (category.getParentCategory() != null && (category.getParentCategory().getId() == null || category.getParentCategory().getId() == 0)) {
            category.setParentCategory(null); // Si no tiene un ID válido, asignarlo a null
        }

        // Guardar la imagen subida
        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile);
            if (fileName != null) {
                category.setImage(fileName); // Guardar el nombre del archivo en la entidad
            }
        }

        categoryRepository.save(category);
        logger.info("Categoría con ID {} actualizada con éxito.", category.getId());
        return "redirect:/categories";
    }

    /**
     * Elimina una categoría de la base de datos.
     *
     * @param id                  ID de la categoría a eliminar.
     * @param redirectAttributes  Atributos para mensajes flash de redirección.
     * @return Redirección a la lista de categorías.
     */
    @PostMapping("/delete")
    public String deleteCategory(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        logger.info("Eliminando categoría con ID {}", id);
        try {
            // Buscar la categoría por su ID
            Optional<Category> category = categoryRepository.findById(id);
            if (category != null) {
                // Eliminar la categoría de la base de datos
                categoryRepository.deleteById(id);
                // Eliminar la imagen asociada, si existe
                if (category.get().getImage() != null && !category.get().getImage().isEmpty()) {
                    fileStorageService.deleteFile(category.get().getImage());
                }
                logger.info("Categoría con ID {} eliminada con éxito.", id);
                redirectAttributes.addFlashAttribute("successMessage", "Categoría eliminada con éxito.");
            } else {
                logger.warn("Categoría con ID {} no encontrada.", id);
                redirectAttributes.addFlashAttribute("errorMessage", "Categoría no encontrada.");
            }
        } catch (Exception e) {
            logger.error("Error al eliminar la categoría con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la categoría.");
        }
        logger.info("Categoría con ID {} eliminada con éxito.", id);
        return "redirect:/categories";
    }

}
