package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.calcul.LongueDureeOffre;
import tn.esprit.autoloc.calcul.StandardOffre;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.calcul.WeekendOffre;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VehiculeServiceImplTest extends GenericServiceTestBase<Vehicule, Long> {
    @Mock private IVehiculeRepository repository;
    @Mock private IAgenceRepository agenceRepository;
    @Mock private IEquipementRepository equipementRepository;
    private VehiculeServiceImpl service;

    @BeforeEach
    void init() {
        service = new VehiculeServiceImpl(repository,
                Map.of("standard", new StandardOffre(), "weekend", new WeekendOffre(), "longueDuree", new LongueDureeOffre()),
                agenceRepository, equipementRepository);
    }

    @Override protected GenericServiceImpl<Vehicule, Long> service() { return service; }
    @Override protected CrudRepository<Vehicule, Long> repository() { return repository; }
    @Override protected Long id() { return 1L; }
    @Override protected void setId(Vehicule v, Long id) { v.setIdVehicule(id); }

    private Vehicule vehicule(String immat, String tarif) {
        Vehicule v = new Vehicule();
        v.setImmatriculation(immat); v.setMarque("Toyota"); v.setModele("Yaris");
        v.setCategorie(CategorieVehicule.CITADINE); v.setStatut(StatutVehicule.DISPONIBLE);
        v.setTarifJournalier(new BigDecimal(tarif));
        return v;
    }

    @Override protected Vehicule valide() { return vehicule("TU-100", "100"); }
    @Override protected Vehicule invalide() { Vehicule v = valide(); v.setImmatriculation(""); return v; }
    @Override protected Vehicule modifiee() {
        Vehicule v = vehicule("TU-200", "250");
        v.setMarque("Isuzu"); v.setModele("DMax"); v.setCategorie(CategorieVehicule.SUV); v.setStatut(StatutVehicule.LOUE);
        return v;
    }
    @Override protected void verifieChamps(Vehicule r, Vehicule s) {
        assertEquals(s.getImmatriculation(), r.getImmatriculation());
        assertEquals(s.getMarque(), r.getMarque());
        assertEquals(s.getModele(), r.getModele());
        assertEquals(s.getCategorie(), r.getCategorie());
        assertEquals(s.getTarifJournalier(), r.getTarifJournalier());
        assertEquals(s.getStatut(), r.getStatut());
    }

    // ---------- règles de validation ----------
    @Test
    void check_tarifNulOuNegatif_estInvalide() {
        Vehicule v = valide();
        v.setTarifJournalier(BigDecimal.ZERO);
        assertFalse(service.check(v, true));
        v.setTarifJournalier(new BigDecimal("-5"));
        assertFalse(service.check(v, true));
    }

    // ---------- calcul du tarif (patron Strategy) ----------
    @Test
    void calculTarif_standard_tarifFois_jours() {
        assertEquals(1000.0, service.calculTarif(valide(), 10, VehiculeOffre.STANDARD), 0.001);
    }

    @Test
    void calculTarif_weekend_remise20pourcent() {
        assertEquals(800.0, service.calculTarif(valide(), 10, VehiculeOffre.WEEKEND), 0.001);
    }

    @Test
    void calculTarif_longueDuree_remise25pourcent() {
        assertEquals(750.0, service.calculTarif(valide(), 10, VehiculeOffre.LONGUE_DUREE), 0.001);
    }

    @Test
    void calculTarif_longueDureeAuDessusDe2000_ajouteRemise5pourcent() {
        // Calcul attendu : 300 x 0,75 x 10 donne 2250, supérieur à 2000, donc 5 % de remise en plus
        assertEquals(2137.5, service.calculTarif(vehicule("TU-300", "300"), 10, VehiculeOffre.LONGUE_DUREE), 0.001);
    }

    @Test
    void calculTarif_offreSansStrategie_leveIllegalArgument() {
        VehiculeServiceImpl sansOffres = new VehiculeServiceImpl(repository, Map.of(), agenceRepository, equipementRepository);

        Vehicule vehicule = valide();

        assertThrows(IllegalArgumentException.class,
                () -> sansOffres.calculTarif(vehicule, 3, VehiculeOffre.STANDARD));
    }

    // ---------- affectations ----------
    @Test
    void desaffecte_retireLAgenceEtEnregistre() {
        Vehicule v = valide();
        v.setAgence(new Agence());
        when(repository.findById(1L)).thenReturn(Optional.of(v));
        when(repository.save(v)).thenReturn(v);

        Vehicule resultat = service.desaffecte(1L);

        assertNull(resultat.getAgence());
        verify(repository).save(v);
    }

    @Test
    void affecte_agenceExistante_associeLeVehicule() {
        Vehicule v = valide();
        Agence agence = new Agence();
        when(repository.findById(1L)).thenReturn(Optional.of(v));
        when(agenceRepository.findById(2L)).thenReturn(Optional.of(agence));
        when(repository.save(v)).thenReturn(v);

        assertSame(agence, service.affecte(1L, 2L).getAgence());
    }

    @Test
    void affecte_agenceInconnue_leveResourceNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(valide()));
        when(agenceRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.affecte(1L, 2L));
        verify(repository, never()).save(any());
    }

    @Test
    void affecte_vehiculeInconnu_leveResourceNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.affecte(1L, 2L));
    }

    @Test
    void affecteEquipemment_ajouteLEquipement() {
        Vehicule v = valide();
        v.setEquipements(new HashSet<>());
        Equipement gps = new Equipement();
        when(repository.findById(1L)).thenReturn(Optional.of(v));
        when(equipementRepository.findById(5L)).thenReturn(Optional.of(gps));
        when(repository.save(v)).thenReturn(v);

        Vehicule resultat = service.affecteEquipemment(1L, 5L);

        assertTrue(resultat.getEquipements().contains(gps));
    }

    @Test
    void affecteEquipemment_equipementInconnu_leveResourceNotFound() {
        Vehicule v = valide();
        v.setEquipements(new HashSet<>());
        when(repository.findById(1L)).thenReturn(Optional.of(v));
        when(equipementRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.affecteEquipemment(1L, 5L));
        verify(repository, never()).save(any());
    }
}
