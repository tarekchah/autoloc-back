package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IMaintenanceRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MaintenanceServiceImplTest extends GenericServiceTestBase<Maintenance, Long> {
    @Mock private IMaintenanceRepository repository;
    private MaintenanceServiceImpl service;

    @BeforeEach
    void init() { service = new MaintenanceServiceImpl(repository); }

    @Override protected GenericServiceImpl<Maintenance, Long> service() { return service; }
    @Override protected CrudRepository<Maintenance, Long> repository() { return repository; }
    @Override protected Long id() { return 1L; }
    @Override protected void setId(Maintenance m, Long id) { m.setIdMaintenance(id); }

    @Override protected Maintenance valide() {
        Maintenance m = new Maintenance();
        m.setDateDebut(LocalDate.of(2026, 4, 1)); m.setDateFin(LocalDate.of(2026, 4, 3));
        m.setDescription("Vidange"); m.setVehicule(new Vehicule());
        return m;
    }
    @Override protected Maintenance invalide() { Maintenance m = valide(); m.setVehicule(null); return m; }
    @Override protected Maintenance modifiee() {
        Maintenance m = new Maintenance();
        m.setDateDebut(LocalDate.of(2026, 6, 1)); m.setDateFin(LocalDate.of(2026, 6, 2));
        m.setDescription("Pneus"); m.setVehicule(new Vehicule());
        return m;
    }
    @Override protected void verifieChamps(Maintenance r, Maintenance s) {
        assertEquals(s.getDateDebut(), r.getDateDebut());
        assertEquals(s.getDateFin(), r.getDateFin());
        assertEquals(s.getDescription(), r.getDescription());
        assertSame(s.getVehicule(), r.getVehicule());
    }

    @Test
    void check_dateFinAvantDateDebut_estInvalide() {
        Maintenance m = valide();
        m.setDateFin(m.getDateDebut().minusDays(1));
        assertFalse(service.check(m, true));
    }

    @Test
    void check_memeJourDebutEtFin_estValide() {
        Maintenance m = valide();
        m.setDateFin(m.getDateDebut());
        assertTrue(service.check(m, true));
    }
}
