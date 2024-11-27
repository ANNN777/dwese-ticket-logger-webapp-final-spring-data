package org.iesalixar.daw2.antonioMerida.dwese_ticket_logger_webapp.controllers;

import org.iesalixar.daw2.antonioMerida.dwese_ticket_logger_webapp.entities.Province;
import org.iesalixar.daw2.antonioMerida.dwese_ticket_logger_webapp.entities.Region;
import org.iesalixar.daw2.antonioMerida.dwese_ticket_logger_webapp.repositories.ProvinceRepository;
import org.iesalixar.daw2.antonioMerida.dwese_ticket_logger_webapp.repositories.RegionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

public class ProvinceControllerTest {

    @Mock
    private ProvinceRepository provinceRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private MessageSource messageSource;  // Agregar Mock para MessageSource

    @InjectMocks
    private ProvinceController provinceController;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    public void setup() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListProvinces() {
        List<Province> provinces = new ArrayList<>();
        provinces.add(new Province());
        provinces.add(new Province());

        //Configurar el comportamiento del mock
        when(provinceRepository.findAll()).thenReturn(provinces);
        //Llamar al método bajo prueba
        String viewName = provinceController.listProvinces(model);

        verify(model).addAttribute("listProvinces", provinces);
        assertEquals("province", viewName);
    }

    @Test
    public void testShowNewForm() {

        List<Region> regions = new ArrayList<>();
        regions.add(new Region());
        regions.add(new Region());

        when(regionRepository.findAll()).thenReturn(regions);
        String viewName = provinceController.showNewForm(model);

        verify(model).addAttribute(eq("province"), any(Province.class));
        verify(model).addAttribute("listRegions", regions);
        assertEquals("province-form", viewName);
    }

    @Test
    public void testInsertProvince() {
        Province province = new Province();
        province.setCode("TEST");

        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        when(provinceRepository.existsProvinceByCode(province.getCode())).thenReturn(false);

        //Llamar al método bajo prueba
        String redirectView = provinceController.insertProvince(province, result,
                mock(RedirectAttributes.class), Locale.getDefault(), model);

        //Verificar que se guarda la provincia y la vista es la correcta
        verify(provinceRepository).save(province);
        assertEquals("redirect:/provinces", redirectView);
    }

    @Test
    public void testInsertProvinceWithValidationErrors() {
        Province province = new Province();

        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        String viewName = provinceController.insertProvince(
                province,
                result,
                mock(RedirectAttributes.class),
                Locale.getDefault(),
                model
        );

        verify(model).addAttribute("listRegions", regionRepository.findAll());
        assertEquals("province-form", viewName);
    }

    @Test
    public void testDeleteProvince() {
        Long provinceId = 1L;

        doNothing().when(provinceRepository).deleteById(provinceId);

        //Llamar al método bajo prueba
        String redirectView = provinceController.deleteProvince(provinceId, redirectAttributes);

        //Verificar que se ha llamado al método deleteById() del repositorio.
        verify(provinceRepository).deleteById(provinceId);
        assertEquals("redirect:/provinces", redirectView);
    }

    @Test
    public void testDeleteProvinceWithException() {
        //Id de la provincia a eliminar
        Long provinceId = 1L;

        doThrow(new RuntimeException("Error al eliminar la provincia")).when(provinceRepository).deleteById(provinceId);

        String redirectView = provinceController.deleteProvince(provinceId, redirectAttributes);

        verify(provinceRepository).deleteById(provinceId);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());

        //Verificar que la lista devuelta sea la correcta
        assertEquals("redirect:/provinces", redirectView);
    }

    @Test
    public void testShowEditForm() {
        //Id de la provincia a editar
        Long provinceId = 1L;
        Province province = new Province();
        province.setId(provinceId);
        province.setCode("TEST");

        List<Region> regions = new ArrayList<>();
        regions.add(new Region());

        when(provinceRepository.findById(provinceId)).thenReturn(Optional.of(province));
        when(regionRepository.findAll()).thenReturn(regions);

        //Llamar al método bajo prueba
        String viewName = provinceController.showEditForm(provinceId, model);

        verify(model).addAttribute("province", province);
        verify(model).addAttribute("listRegions", regions);

        //Verificar que la lista devuelta sea la correcta
        assertEquals("province-form", viewName);
    }

    @Test
    public void testUpdateProvince() {
        Province province = new Province();
        province.setId(1L);
        province.setCode("TEST");

        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        when(provinceRepository.existsProvinceByCodeAndNotId(province.getCode(), province.getId())).thenReturn(false);

        String redirectView = provinceController.updateProvince(province, result,
                mock(RedirectAttributes.class), Locale.getDefault(), model);

        verify(provinceRepository).save(province);
        assertEquals("redirect:/provinces", redirectView);
    }

    @Test
    public void testUpdateProvinceWithValidationErrors() {
        Province province = new Province();
        province.setId(1L);

        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        List<Region> regions = new ArrayList<>();
        regions.add(new Region());

        when(regionRepository.findAll()).thenReturn(regions);

        String viewName = provinceController.updateProvince(province, result,
                mock(RedirectAttributes.class), Locale.getDefault(), model);

        verify(model).addAttribute("listRegions", regions);
        assertEquals("province-form", viewName);
    }

    @Test
    public void testUpdateProvinceWithExistingCode() {
        Province province = new Province();
        province.setId(1L);
        province.setCode("DUPLICATE");

        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        // Simular que el código ya existe
        when(provinceRepository.existsProvinceByCodeAndNotId(province.getCode(), province.getId())).thenReturn(true);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(messageSource.getMessage("msg.province-controller.update.codeExist", null, Locale.getDefault()))
                .thenReturn("Código ya existente.");

        String redirectView = provinceController.updateProvince(province, result, redirectAttributes, Locale.getDefault(), model);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "Código ya existente.");
        assertEquals("redirect:/provinces/edit?id=" + province.getId(), redirectView);
    }
}
