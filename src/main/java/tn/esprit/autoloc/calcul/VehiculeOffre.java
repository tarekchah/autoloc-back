package tn.esprit.autoloc.calcul;

public enum VehiculeOffre {
    STANDARD("standard"),
    WEEKEND("weekend"),
    LONGUE_DUREE("longueDuree");

    private final String nomBean;

    VehiculeOffre(String nomBean) {
        this.nomBean = nomBean;
    }

    /** Nom du bean {@link OffreStrategy} qui calcule cette offre. */
    public String getNomBean() {
        return nomBean;
    }
}
