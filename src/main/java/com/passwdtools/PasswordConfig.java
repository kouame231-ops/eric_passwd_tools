package com.passwdtools;

/**
 * Represente les choix de l'utilisateur pour la generation d'un mot de passe :
 * la longueur souhaitee et les categories de caracteres a utiliser.
 */
public class PasswordConfig {

    private int longueur;
    private boolean avecMajuscules;
    private boolean avecMinuscules;
    private boolean avecChiffres;
    private boolean avecSymboles;

    public PasswordConfig(int longueur, boolean avecMajuscules, boolean avecMinuscules,
                           boolean avecChiffres, boolean avecSymboles) {

        if (longueur < 4) {
            throw new IllegalArgumentException("La longueur doit etre d'au moins 4 caracteres.");
        }
        if (!avecMajuscules && !avecMinuscules && !avecChiffres && !avecSymboles) {
            throw new IllegalArgumentException("Il faut activer au moins une categorie de caracteres.");
        }

        this.longueur = longueur;
        this.avecMajuscules = avecMajuscules;
        this.avecMinuscules = avecMinuscules;
        this.avecChiffres = avecChiffres;
        this.avecSymboles = avecSymboles;
    }

    public int getLongueur() {
        return longueur;
    }

    public boolean isAvecMajuscules() {
        return avecMajuscules;
    }

    public boolean isAvecMinuscules() {
        return avecMinuscules;
    }

    public boolean isAvecChiffres() {
        return avecChiffres;
    }

    public boolean isAvecSymboles() {
        return avecSymboles;
    }

    public int nombreCategoriesActives() {
        int total = 0;
        if (avecMajuscules) total++;
        if (avecMinuscules) total++;
        if (avecChiffres) total++;
        if (avecSymboles) total++;
        return total;
    }
}