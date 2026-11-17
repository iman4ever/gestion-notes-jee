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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String libelle;

    @ManyToOne
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    @ManyToOne
    @JoinColumn(name = "niveau_id", nullable = false)
    private Niveau niveau;

    @ManyToOne
    @JoinColumn(name = "annee_universitaire_id", nullable = false)
    private AnneeUniversitaire anneeUniversitaire;

    @OneToMany(mappedBy = "promotion", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Inscription> inscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "promotion", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Enseignement> enseignements = new ArrayList<>();

    public Promotion() {
    }

    public Promotion(String code, String libelle, Filiere filiere, Niveau niveau, AnneeUniversitaire anneeUniversitaire) {
        this.code = code;
        this.libelle = libelle;
        this.filiere = filiere;
        this.niveau = niveau;
        this.anneeUniversitaire = anneeUniversitaire;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Filiere getFiliere() {
        return filiere;
    }

    public void setFiliere(Filiere filiere) {
        this.filiere = filiere;
    }

    public Niveau getNiveau() {
        return niveau;
    }

    public void setNiveau(Niveau niveau) {
        this.niveau = niveau;
    }

    public AnneeUniversitaire getAnneeUniversitaire() {
        return anneeUniversitaire;
    }

    public void setAnneeUniversitaire(AnneeUniversitaire anneeUniversitaire) {
        this.anneeUniversitaire = anneeUniversitaire;
    }

    public List<Inscription> getInscriptions() {
        return inscriptions;
    }

    public void setInscriptions(List<Inscription> inscriptions) {
        this.inscriptions = inscriptions;
    }

    public List<Enseignement> getEnseignements() {
        return enseignements;
    }

    public void setEnseignements(List<Enseignement> enseignements) {
        this.enseignements = enseignements;
    }

    public void ajouter() {
    }

    public void modifier() {
    }

    public void supprimer() {
    }

    public void consulter() {
    }
}
