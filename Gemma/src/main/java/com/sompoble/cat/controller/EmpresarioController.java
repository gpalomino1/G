package com.sompoble.cat.controller;

import com.sompoble.cat.domain.Empresario;
import com.sompoble.cat.service.EmpresarioService;
import com.sompoble.cat.exception.ResourceNotFoundException;
import com.sompoble.cat.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empresarios")
public class EmpresarioController {

    @Autowired
    private EmpresarioService empresarioService;

    // Obtener todos los empresarios
    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Empresario> empresarios = empresarioService.findAll();
        if (empresarios.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron empresarios en la base de datos");
        }
        return ResponseEntity.ok(empresarios);
    }

    // Consultar por DNI
    @GetMapping("/{dni}")
    public ResponseEntity<?> getByDni(@PathVariable String dni) {
        try {
        Empresario empresario = empresarioService.findByDNI(dni);
            return ResponseEntity.ok(empresario);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Empresario con DNI " + dni + " no encontrado");
        }
    }

    // Crear un nuevo empresario
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Empresario empresario) {
        if (empresarioService.existsByDni(empresario.getDni())) {
            throw new BadRequestException("Empresario con DNI " + empresario.getDni() + " ya existe");
        } else if (empresarioService.existsByEmail(empresario.getEmail())) {
            throw new BadRequestException("Email " + empresario.getEmail() + " ya está registrado");
        }

        empresarioService.addEmpresario(empresario);
        return ResponseEntity.created(null).build(); 
    }

    // Actualizar un empresario
    @PutMapping("/{dni}")
    public ResponseEntity<?> update(@PathVariable String dni, @RequestBody Map<String, Object> updates) {
        try{
        Empresario existingEmpresario = empresarioService.findByDNI(dni);
        updates.forEach((key, value) -> {
            if (value != null) {
                switch (key) {
                    case "dni" -> existingEmpresario.setDni(value.toString());
                    case "nombre" -> existingEmpresario.setNombre(value.toString());
                    case "apellidos" -> existingEmpresario.setApellidos(value.toString());
                    case "telefono" -> existingEmpresario.setTelefono(value.toString());
                    case "pass" -> existingEmpresario.setPass(value.toString());
                    case "email" -> existingEmpresario.setEmail(value.toString());
                }
            }
        });

        empresarioService.updateEmpresario(existingEmpresario);
        return ResponseEntity.ok("Empresario con DNI " + dni + " actualizado correctamente");
        } catch (Exception e) {
            throw new ResourceNotFoundException("No se encontró un empresario con el DNI " + dni);
        }
    }

    // Eliminar un empresario por DNI
    @DeleteMapping("/{dni}")
    public ResponseEntity<?> delete(@PathVariable String dni) {
        if (!empresarioService.existsByDni(dni)) {
            throw new ResourceNotFoundException("No se encontró un empresario con el DNI " + dni);
        }

        empresarioService.deleteByDni(dni);
        return ResponseEntity.ok("Empresario con DNI " + dni + " eliminado correctamente");
    }
}