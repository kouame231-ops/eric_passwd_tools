package com.passwdtools;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Genere des mots de passe aleatoires a partir d'une PasswordConfig.
 * Utilise SecureRandom (et non Random) car il s'agit de generer des
 * secrets : Random est previsible, SecureRandom puise dans une source
 * d'entropie adaptee a un usage lie a la securite.
 */
public class PasswordGenerator {

    private static final String MAJUSCULES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String MINUSCULES = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHIFFRES = "0123456789";
    private static final String SYMBOLES = "!@#$%^&*()-_=+";

    private final SecureRandom random = new SecureRandom();

    public String genererMotDePasse(PasswordConfig config) {
        String alphabetComplet = construireAlphabet(config);
        List<Character> caracteres = new ArrayList<>();

        // On garantit un caractere de chaque categorie active, pour ne
        // jamais risquer qu'une categorie demandee soit absente par hasard.
        if (config.isAvecMajuscules()) {
            caracteres.add(tirerUnCaractere(MAJUSCULES));
        }
        if (config.isAvecMinuscules()) {
            caracteres.add(tirerUnCaractere(MINUSCULES));
        }
        if (config.isAvecChiffres()) {
            caracteres.add(tirerUnCaractere(CHIFFRES));
        }
        if (config.isAvecSymboles()) {
            caracteres.add(tirerUnCaractere(SYMBOLES));
        }

        // On complete jusqu'a la longueur demandee avec l'alphabet complet.
        while (caracteres.size() < config.getLongueur()) {
            caracteres.add(tirerUnCaractere(alphabetComplet));
        }

        melanger(caracteres);

        StringBuilder motDePasse = new StringBuilder();
        for (char c : caracteres) {
            motDePasse.append(c);
        }
        return motDePasse.toString();
    }

    public List<String> genererPlusieurs(PasswordConfig config, int quantite) {
        List<String> resultats = new ArrayList<>();
        for (int i = 0; i < quantite; i++) {
            resultats.add(genererMotDePasse(config));
        }
        return resultats;
    }

    private String construireAlphabet(PasswordConfig config) {
        StringBuilder alphabet = new StringBuilder();
        if (config.isAvecMajuscules()) alphabet.append(MAJUSCULES);
        if (config.isAvecMinuscules()) alphabet.append(MINUSCULES);
        if (config.isAvecChiffres()) alphabet.append(CHIFFRES);
        if (config.isAvecSymboles()) alphabet.append(SYMBOLES);
        return alphabet.toString();
    }

    private char tirerUnCaractere(String source) {
        int indexAleatoire = random.nextInt(source.length());
        return source.charAt(indexAleatoire);
    }

    // Algorithme de Fisher-Yates : melange une liste de facon
    // uniformement aleatoire, en parcourant la liste de la fin vers le debut
    // et en echangeant chaque element avec un element pris au hasard
    // parmi ceux qui restent a traiter.
    private void melanger(List<Character> liste) {
        for (int i = liste.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Character temp = liste.get(i);
            liste.set(i, liste.get(j));
            liste.set(j, temp);
        }
    }
}