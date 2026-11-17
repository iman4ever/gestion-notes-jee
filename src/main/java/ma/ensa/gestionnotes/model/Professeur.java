package ma.ensa.gestionnotes.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "professeurs")
public class Professeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_professeur", nullable = false, unique = true)
    private String idProfesseur;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String specialite;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "professeur", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Enseignement> enseignements = new ArrayList<>();

    public Professeur() {
    }

    public Professeur(String idProfesseur, String nom, String prenom, String specialite, Utilisateur utilisateur) {
        this.idProfesseur = idProfesseur;
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.utilisateur = utilisateur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdProfesseur() {
        return idProfesseur;
    }

    public void setIdProfesseur(String idProfesseur) {
        this.idProfesseur = idProfesseur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public List<Enseignement> getEnseignements() {
        return enseignements;
    }

    public void setEnseignements(List<Enseignement> enseignements) {
        this.enseignements = enseignements;
    }

    public void consulterEnseignements() {
    }

    public void saisirNote() {
    }

    public void modifierNote() {
    }

    public void consulterResultats() {
    }
}
