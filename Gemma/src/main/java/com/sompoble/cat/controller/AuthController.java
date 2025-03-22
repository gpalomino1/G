package com.sompoble.cat.controller;

import com.sompoble.cat.domain.Cliente;
import com.sompoble.cat.domain.Empresario;
import com.sompoble.cat.exception.BadRequestException;
import com.sompoble.cat.exception.UnauthorizedException;
import com.sompoble.cat.service.ClienteService;
import com.sompoble.cat.service.EmpresarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class AuthController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EmpresarioService empresarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String pass = loginRequest.get("pass");

        if (email == null || pass == null) {
            throw new BadRequestException("Email y contraseña son obligatorios");
        }

        email = email.toLowerCase();
        Map<String, Object> response = new HashMap<>();

        Cliente cliente = clienteService.findByEmail(email);
        Empresario empresario = empresarioService.findByEmail(email);

        if (cliente != null && empresario != null && passwordEncoder.matches(pass, cliente.getPass())) {
            return crearRespuestaExitosa(response, 3, cliente);
        }

        if (cliente != null && passwordEncoder.matches(pass, cliente.getPass())) {
            return crearRespuestaExitosa(response, 1, cliente);
        }

        if (empresario != null && passwordEncoder.matches(pass, empresario.getPass())) {
            return crearRespuestaExitosa(response, 2, empresario);
        }

        if (cliente == null && empresario == null) {
            throw new BadRequestException("El usuario introducido no se encuentra dado de alta en la plataforma");
        } else {
            throw new UnauthorizedException("Credenciales incorrectas");
        }
    }

    private ResponseEntity<?> crearRespuestaExitosa(Map<String, Object> response, int tipoUsuario, Object usuario) {
        response.put("status", 200);
        response.put("message", "Inicio de sesión exitoso");
        response.put("tipoUsuario", tipoUsuario);
        response.put("usuario", usuario);
        return ResponseEntity.ok(response);
    }
}
