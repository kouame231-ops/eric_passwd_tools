package com.passwdtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Pilote le conteneur Docker contenant CrackLib, via "docker exec"
 * (et non via une API REST). ProcessBuilder permet de lancer un
 * processus externe (ici, la commande "docker" elle-meme) et de
 * communiquer avec lui par ses flux standard.
 */
public class SecurityAuditClient {

    private final String nomConteneur;

    public SecurityAuditClient(String nomConteneur) {
        this.nomConteneur = nomConteneur;
    }

    /**
     * Verifie que le conteneur est demarre et accessible, avant de
     * lancer un veritable audit.
     */
    public boolean conteneurAccessible() {
        try {
            ProcessBuilder pb = new ProcessBuilder("docker", "exec", nomConteneur, "true");
            Process process = pb.start();
            int code = process.waitFor();
            return code == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    /**
     * Envoie le mot de passe a cracklib-check, a l'interieur du
     * conteneur, et retourne la ligne de sortie brute produite par
     * l'outil. Le mot de passe est transmis via l'entree standard
     * (stdin), pas en argument, pour ne pas apparaitre dans la liste
     * des processus du systeme (visible via la commande ps).
     */
    public String auditer(String motDePasse) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "exec", "-i", nomConteneur, "cracklib-check");
        Process process = pb.start();

        try (OutputStream stdin = process.getOutputStream()) {
            stdin.write((motDePasse + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            stdin.flush();
        }

        String sortie = lireSortie(process.getInputStream());
        process.waitFor();
        return sortie;
    }

    private String lireSortie(java.io.InputStream entree) throws IOException {
        StringBuilder resultat = new StringBuilder();
        try (BufferedReader lecteur = new BufferedReader(new InputStreamReader(entree, StandardCharsets.UTF_8))) {
            String ligne;
            while ((ligne = lecteur.readLine()) != null) {
                resultat.append(ligne);
            }
        }
        return resultat.toString();
    }
}