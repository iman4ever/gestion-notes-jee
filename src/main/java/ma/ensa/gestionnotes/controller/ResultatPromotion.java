package ma.ensa.gestionnotes.controller;

public class ResultatPromotion {
    private final String cne;
    private final String nom;
    private final String prenom;
    private final int nombreNotes;
    private final double moyenne;
    private final String resultat;

    public ResultatPromotion(String cne, String nom, String prenom, int nombreNotes, double moyenne, String resultat) {
        this.cne = cne;
        this.nom = nom;
        this.prenom = prenom;
        this.nombreNotes = nombreNotes;
        this.moyenne = moyenne;
        this.resultat = resultat;
    }

    public String getCne() {
        return cne;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public int getNombreNotes() {
        return nombreNotes;
    }

    public double getMoyenne() {
        return moyenne;
    }

    public String getResultat() {
        return resultat;
    }
}
