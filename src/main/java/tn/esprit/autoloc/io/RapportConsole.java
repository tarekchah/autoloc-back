package tn.esprit.autoloc.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tn.esprit.autoloc.domain.Agence;

public class RapportConsole implements Rapport {
    private static final Logger LOG = LoggerFactory.getLogger(RapportConsole.class);

    private final Rapport delegue;
    public RapportConsole(Rapport delegue) { this.delegue = delegue; }
    public void entete(Agence a) {
        LOG.info("Agence #{} - {} - Tel : {}", a.getIdAgence(), a.getNom(), a.getTelephone());
    }
    public void total(double t) { delegue.total(t); }
    public String contenu() { return delegue.contenu(); }
}
