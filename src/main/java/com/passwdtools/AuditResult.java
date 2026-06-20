package com.passwdtools;

/**
 * Resultat d'un audit de securite pour un mot de passe donne.
 * Traduit le verdict brut de CrackLib en un niveau parmi les 5 demandes.
 */
public class AuditResult {

    public enum Niveau {
        TRES_FAIBLE("Tres faible"),
        FAIBLE("Faible"),
        MOYEN("Moyen"),
        FORT("Fort"),
        TRES_FORT("Tres fort");

        private final String libelle;

        Niveau(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private final boolean accepteParCracklib;
    private final String motifRejet;
    private final Niveau niveau;

    private AuditResult(boolean accepteParCracklib, String motifRejet, Niveau niveau) {
        this.accepteParCracklib = accepteParCracklib;
        this.motifRejet = motifRejet;
        this.niveau = niveau;
    }

    public boolean isAccepteParCracklib() {
        return accepteParCracklib;
    }

    public String getMotifRejet() {
        return motifRejet;
    }

    public Niveau getNiveau() {
        return niveau;
    }

    /**
     * Construit un AuditResult a partir de la sortie brute de
     * cracklib-check (format : "<motdepasse>: OK" ou
     * "<motdepasse>: <motif de rejet>").
     */
    public static AuditResult depuisSortieCracklib(String sortieBrute, String motDePasse, int nombreCategories) {
        String verdict = extraireVerdict(sortieBrute);
        boolean accepte = verdict.equalsIgnoreCase("OK");

        if (!accepte) {
            Niveau niveau = mapperMotifVersNiveau(verdict);
            return new AuditResult(false, verdict, niveau);
        }

        Niveau niveau = departagerSiAccepte(motDePasse.length(), nombreCategories);
        return new AuditResult(true, null, niveau);
    }

    private static String extraireVerdict(String sortieBrute) {
        int indexDeuxPoints = sortieBrute.indexOf(':');
        if (indexDeuxPoints == -1) {
            return sortieBrute.trim();
        }
        return sortieBrute.substring(indexDeuxPoints + 1).trim();
    }

    // Le motif precis fourni par CrackLib determine directement le niveau,
    // sans calcul Java independant. Les motifs critiques (dictionnaire,
    // trop court) sont distingues des autres motifs de rejet.
    private static Niveau mapperMotifVersNiveau(String motif) {
        String motifMinuscule = motif.toLowerCase();
        boolean critique = motifMinuscule.contains("way too short")
                || motifMinuscule.contains("dictionary word")
                || motifMinuscule.contains("whitespace");
        return critique ? Niveau.TRES_FAIBLE : Niveau.FAIBLE;
    }

    // CrackLib ne distingue rien au-dela de "OK" : ce departage est
    // necessaire pour exploiter les 3 niveaux superieurs, et n'intervient
    // qu'apres validation par l'outil externe.
    private static Niveau departagerSiAccepte(int longueur, int nombreCategories) {
        int points = 0;

        if (longueur >= 16) points += 2;
        else if (longueur >= 12) points += 1;

        if (nombreCategories >= 4) points += 2;
        else if (nombreCategories == 3) points += 1;

        if (points <= 0) return Niveau.MOYEN;
        if (points <= 2) return Niveau.FORT;
        return Niveau.TRES_FORT;
    }
}