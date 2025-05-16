package com.taskcolab.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Déclare une classe globale de gestion des exceptions pour tous les contrôleurs.
// @RestControllerAdvice combine @ControllerAdvice et @ResponseBody, permettant de retourner des réponses JSON.
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // Gère spécifiquement les exceptions de type ResourceNotFoundException (ressource non trouvée).
    // Retourne une réponse HTTP 404 (NOT_FOUND) avec un message d'erreur.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        // ApiResponse.error() est une méthode utilitaire pour structurer la réponse d'erreur.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    // Gère les erreurs de validation des champs (ex: @Valid dans les DTOs).
    // Se déclenche quand une validation échoue (MethodArgumentNotValidException).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        // Crée une Map pour stocker les erreurs par champ (nom du champ -> message d'erreur).
        Map<String, String> errors = new HashMap<>();
        
        // Parcourt toutes les erreurs de validation et les ajoute à la Map.
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        // Retourne une réponse HTTP 400 (BAD_REQUEST) avec la Map des erreurs.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("validation echoue " + ex.getMessage()));
    }

    // Gère toutes les autres exceptions non capturées (fallback).
    // Retourne une réponse HTTP 500 (INTERNAL_SERVER_ERROR).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        // Loguer cette erreur est recommandé pour le débogage.
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("une erreur: " + ex.getMessage()));
    }
}

