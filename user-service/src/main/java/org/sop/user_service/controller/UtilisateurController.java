package org.sop.user_service.controller;

//import org.sop.subscription_service.model.Abonnement;
import org.sop.user_service.Utils.TokenBlacklist;
import org.sop.user_service.DTO.ConnexionRequest;
import org.sop.user_service.DTO.UtilisateurDTO;
import org.sop.user_service.Utils.JwtUtil;
import org.sop.user_service.model.Utilisateur;
import org.sop.user_service.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UtilisateurService utilisateurService;

    // Inscription d'un utilisateur
    @PostMapping("/inscrire")
    public ResponseEntity<UtilisateurDTO> inscrire(@RequestBody UtilisateurDTO utilisateurDTO) {
        UtilisateurDTO utilisateurCree = utilisateurService.inscrire(utilisateurDTO);
        return ResponseEntity.ok(utilisateurCree);
    }

    // Connexion de l'utilisateur
    @PostMapping("/connexion")
    public ResponseEntity<String> connexion(@RequestBody ConnexionRequest request) {
        Optional<UtilisateurDTO> utilisateur = utilisateurService.connexion(request.getNomUtilisateur(), request.getMotDePasse());
        if (utilisateur.isPresent()) {
            String token = jwtUtil.generateToken(request.getNomUtilisateur());
            return ResponseEntity.ok(token); // Assurez-vous que le token est une chaîne valide.
        } else {
            return ResponseEntity.status(401).body("Nom d'utilisateur ou mot de passe incorrect");
        }
    }


    // Déconnexion de l'utilisateur
    @PostMapping("/deconnexion")
    public ResponseEntity<String> deconnexion(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);  // Enlever "Bearer " du début du token
        TokenBlacklist.addToken(token);          // Ajouter à la liste noire
        return ResponseEntity.ok("Déconnexion réussie");
    }

    // Obtenir tous les utilisateurs
    @GetMapping
    public ResponseEntity<List<UtilisateurDTO>> obtenirTousLesUtilisateurs() {
        List<UtilisateurDTO> utilisateurs = utilisateurService.obtenirTousLesUtilisateurs();
        return ResponseEntity.ok(utilisateurs);
    }

    // Obtenir un utilisateur par son nom
    @GetMapping("/{nomUtilisateur}")
    public ResponseEntity<UtilisateurDTO> obtenirUtilisateurParNom(@PathVariable String nomUtilisateur) {
        Optional<UtilisateurDTO> utilisateur = utilisateurService.trouverParNomUtilisateur(nomUtilisateur);
        return utilisateur.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(null));
    }

    // Ajouter un utilisateur
    @PostMapping
    public ResponseEntity<UtilisateurDTO> ajouterUtilisateur(@RequestBody UtilisateurDTO utilisateurDTO) {
        UtilisateurDTO utilisateurCree = utilisateurService.ajouterUtilisateur(utilisateurDTO);
        return ResponseEntity.ok(utilisateurCree);
    }

    // Modifier un utilisateur
    @PutMapping("/{nomUtilisateur}")
    public ResponseEntity<UtilisateurDTO> modifierUtilisateur(@PathVariable String nomUtilisateur, @RequestBody UtilisateurDTO utilisateurDTO) {
        try {
            UtilisateurDTO utilisateurMisAJour = utilisateurService.modifierUtilisateur(nomUtilisateur, utilisateurDTO);
            return ResponseEntity.ok(utilisateurMisAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // Obtenir un utilisateur par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable int id) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurById(id);
        if (utilisateur != null) {
            return ResponseEntity.ok(utilisateur);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer un utilisateur par nom d'utilisateur
    @DeleteMapping("/{nomUtilisateur}")
    public ResponseEntity<String> supprimerUtilisateurParNom(@PathVariable String nomUtilisateur) {
        try {
            utilisateurService.supprimerUtilisateurParNom(nomUtilisateur);
            return ResponseEntity.ok("Utilisateur supprimé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Souscrire à un abonnement
 /*   @PostMapping("/{idUtilisateur}/souscrire")
    public ResponseEntity<Abonnement> souscrireAbonnement(@PathVariable int idUtilisateur, @RequestBody String planAbonnement) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurById(idUtilisateur);
        if (utilisateur != null) {
            // Assurez-vous que le planAbonnement est bien passé dans le corps de la requête
            Abonnement abonnement = utilisateurService.souscrireAbonnement(utilisateur, planAbonnement);
            return ResponseEntity.ok(abonnement);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    // Annuler un abonnement
    @DeleteMapping("/{idUtilisateur}/annuler/{idAbonnement}")
    public ResponseEntity<Boolean> annulerAbonnement(@PathVariable int idUtilisateur, @PathVariable String idAbonnement) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurById(idUtilisateur);
        if (utilisateur != null) {
            boolean annule = utilisateurService.annulerAbonnement(utilisateur, idAbonnement);
            return ResponseEntity.ok(annule);
        } else {
            return ResponseEntity.status(404).body(false);
        }
    }*/
}
