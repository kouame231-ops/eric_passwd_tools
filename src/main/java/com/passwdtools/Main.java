package com.passwdtools;

import java.util.List;
import java.util.Scanner;

/**
 * Point d'entree de l'application. Gere le menu interactif dans le
 * terminal et orchestre les autres classes.
 */
public class Main {

    private static final String NOM_CONTENEUR = "password-audit-cracklib";

    public static void main(String[] args) {
        Scanner saisie = new Scanner(System.in);
        SecurityAuditClient client = new SecurityAuditClient(NOM_CONTENEUR);

        System.out.println("=== Generateur et auditeur de mots de passe (Java + CrackLib/Docker) ===");

        if (!client.conteneurAccessible()) {
            System.out.println("ATTENTION : le conteneur '" + NOM_CONTENEUR + "' n'est pas accessible.");
            System.out.println("Lancez-le avec : docker compose up --build -d");
        }

        PasswordConfig config = demanderConfiguration(saisie);
        int quantite = demanderQuantite(saisie);

        PasswordGenerator generateur = new PasswordGenerator();
        List<String> motsDePasse = generateur.genererPlusieurs(config, quantite);

        System.out.println();
        System.out.println("Resultats :");
        for (String mdp : motsDePasse) {
            afficherAvecAudit(mdp, config, client);
        }

        saisie.close();
    }

    private static PasswordConfig demanderConfiguration(Scanner saisie) {
        while (true) {
            try {
                System.out.print("Longueur du mot de passe (minimum 4) : ");
                int longueur = Integer.parseInt(saisie.nextLine().trim());

                boolean majuscules = demanderOuiNon(saisie, "Inclure des majuscules ? (o/n) : ");
                boolean minuscules = demanderOuiNon(saisie, "Inclure des minuscules ? (o/n) : ");
                boolean chiffres = demanderOuiNon(saisie, "Inclure des chiffres ? (o/n) : ");
                boolean symboles = demanderOuiNon(saisie, "Inclure des symboles ? (o/n) : ");

                return new PasswordConfig(longueur, majuscules, minuscules, chiffres, symboles);
            } catch (NumberFormatException e) {
                System.out.println("Saisis un nombre entier valide pour la longueur.");
            } catch (IllegalArgumentException e) {
                System.out.println("Configuration invalide : " + e.getMessage());
            }
        }
    }

    private static boolean demanderOuiNon(Scanner saisie, String question) {
        System.out.print(question);
        String reponse = saisie.nextLine().trim().toLowerCase();
        return reponse.startsWith("o");
    }

    private static int demanderQuantite(Scanner saisie) {
        System.out.print("Combien de mots de passe generer (mode rafale) ? [1] : ");
        String reponse = saisie.nextLine().trim();
        if (reponse.isEmpty()) {
            return 1;
        }
        try {
            return Math.max(1, Integer.parseInt(reponse));
        } catch (NumberFormatException e) {
            System.out.println("Valeur invalide, generation d'un seul mot de passe.");
            return 1;
        }
    }

    private static void afficherAvecAudit(String motDePasse, PasswordConfig config, SecurityAuditClient client) {
        System.out.print("- " + motDePasse + "  =>  ");
        try {
            String sortieBrute = client.auditer(motDePasse);
            AuditResult resultat = AuditResult.depuisSortieCracklib(
                    sortieBrute, motDePasse, config.nombreCategoriesActives());
            System.out.println(resultat.getNiveau().getLibelle());
        } catch (Exception e) {
            System.out.println("Audit indisponible (conteneur Docker injoignable : " + e.getMessage() + ")");
        }
    }
}