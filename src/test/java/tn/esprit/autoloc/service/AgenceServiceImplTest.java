package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.RoleEmploye;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.io.RapportFactory;
import tn.esprit.autoloc.repository.IAgenceRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AgenceServiceImplTest extends GenericServiceTestBase<Agence, Long> {
    @Mock private IAgenceRepository repository;
    @Mock private IVehiculeService vehiculeService;
    @Mock private IEmployeService employeService;
    private AgenceServiceImpl service;

    @BeforeEach
    void init() { service = new AgenceServiceImpl(repository, vehiculeService, new RapportFactory(), employeService); }

    @Override protected GenericServiceImpl<Agence, Long> service() { return service; }
    @Override protected CrudRepository<Agence, Long> repository() { return repository; }
    @Override protected Long id() { return 1L; }
    @Override protected void setId(Agence a, Long id) { a.setIdAgence(id); }

    private Agence agence(String nom, String tel) {
        Agence a = new Agence();
        a.setNom(nom); a.setTelephone(tel); a.setVille("Tunis"); a.setAdresse("1 Rue Hedi");
        return a;
    }

    @Override protected Agence valide() { return agence("Agence Ariana", "71585874"); }
    @Override protected Agence invalide() { return agence("", "71585874"); }
    @Override protected Agence modifiee() {
        Agence a = agence("Agence Lac", "71123456");
        a.setVille("Sousse"); a.setAdresse("5 Rue du Lac");
        return a;
    }
    @Override protected void verifieChamps(Agence r, Agence s) {
        assertEquals(s.getNom(), r.getNom());
        assertEquals(s.getAdresse(), r.getAdresse());
        assertEquals(s.getVille(), r.getVille());
        assertEquals(s.getTelephone(), r.getTelephone());
    }

    @Test
    void check_telephoneAbsent_estInvalide() {
        assertFalse(service.check(agence("Agence", null), true));
    }

    // ---------- hasVehicules ----------
    @Test
    void hasVehicules_agenceAvecVehicules_retourneVrai() {
        Agence a = valide();
        a.setVehicules(Set.of(new Vehicule()));
        when(repository.findById(1L)).thenReturn(Optional.of(a));

        assertTrue(service.hasVehicules(1L));
    }

    @Test
    void hasVehicules_agenceSansVehicules_retourneFaux() {
        Agence a = valide();
        a.setVehicules(Set.of());
        when(repository.findById(1L)).thenReturn(Optional.of(a));

        assertFalse(service.hasVehicules(1L));
    }

    @Test
    void hasVehicules_listeNulle_retourneFaux() {
        Agence a = valide();
        a.setVehicules(null);
        when(repository.findById(1L)).thenReturn(Optional.of(a));

        assertFalse(service.hasVehicules(1L));
    }

    // ---------- simulation (patrons Factory + Decorator) ----------
    @Test
    void simuler_additionneLesTarifsDesVehicules() {
        Agence a = valide();
        a.setVehicules(new HashSet<>(Set.of(new Vehicule(), new Vehicule())));
        when(vehiculeService.calculTarif(any(Vehicule.class), eq(5), eq(VehiculeOffre.WEEKEND))).thenReturn(100.0);

        String rapport = service.simuler(a, 5, VehiculeOffre.WEEKEND);

        assertTrue(rapport.contains("Total des tarifs : " + String.format("%.2f", 200.0)), rapport);
    }

    @Test
    void simuler_agenceSansVehicules_totalNul() {
        Agence a = valide();
        a.setVehicules(null);

        String rapport = service.simuler(a, 5, VehiculeOffre.STANDARD);

        assertTrue(rapport.contains("Total des tarifs : " + String.format("%.2f", 0.0)), rapport);
        verifyNoInteractions(vehiculeService);
    }

    // ---------- mise à jour avec employés (transaction) ----------
    @Test
    void updateAvecEmployes_metAJourEtInsereLesEmployes() {
        Agence existante = valide();
        existante.setIdAgence(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existante));
        when(repository.save(any(Agence.class))).thenAnswer(inv -> inv.getArgument(0));

        Agence entree = modifiee();
        Employe e1 = employe("A"), e2 = employe("B");
        entree.setEmployes(new HashSet<>(Set.of(e1, e2)));

        Agence resultat = service.updateAvecEmployes(1L, entree);

        assertEquals("Agence Lac", resultat.getNom());
        assertEquals(2, resultat.getEffectif());
        assertSame(resultat, e1.getAgence());
        assertSame(resultat, e2.getAgence());
        verify(employeService).create(e1);
        verify(employeService).create(e2);
    }

    @Test
    void updateAvecEmployes_agenceInconnue_neCreeAucunEmploye() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.updateAvecEmployes(1L, modifiee()));
        verifyNoInteractions(employeService);
    }

    @Test
    void updateAvecEmployes_employeInvalide_propageLErreurPourLeRollback() {
        Agence existante = valide();
        when(repository.findById(1L)).thenReturn(Optional.of(existante));
        when(repository.save(any(Agence.class))).thenAnswer(inv -> inv.getArgument(0));
        when(employeService.create(any(Employe.class))).thenThrow(new IllegalArgumentException("champs obligatoires !!!"));
        Agence entree = modifiee();
        entree.setEmployes(new HashSet<>(Set.of(employe("A"))));

        assertThrows(IllegalArgumentException.class, () -> service.updateAvecEmployes(1L, entree));
    }

    private Employe employe(String nom) {
        Employe e = new Employe();
        e.setNom(nom); e.setPrenom("P"); e.setRole(RoleEmploye.AGENT);
        return e;
    }
}
