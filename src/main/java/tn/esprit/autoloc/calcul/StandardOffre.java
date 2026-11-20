package tn.esprit.autoloc.calcul;

import org.springframework.stereotype.Component;

@Component("standard")   // nom du bean = VehiculeOffre.STANDARD.getNomBean()
public class StandardOffre implements OffreStrategy {
    @Override
    public double calculerTotal(double tarifJournalier, int nbJours) {
        return tarifJournalier * nbJours;
    }
}
