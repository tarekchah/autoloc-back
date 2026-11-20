package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IReservationRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceImplTest extends GenericServiceTestBase<Reservation, Long> {
    @Mock private IReservationRepository repository;
    private ReservationServiceImpl service;

    @BeforeEach
    void init() { service = new ReservationServiceImpl(repository); }

    @Override protected GenericServiceImpl<Reservation, Long> service() { return service; }
    @Override protected CrudRepository<Reservation, Long> repository() { return repository; }
    @Override protected Long id() { return 1L; }
    @Override protected void setId(Reservation r, Long id) { r.setIdReservation(id); }

    @Override protected Reservation valide() {
        Reservation r = new Reservation();
        r.setDateDebut(LocalDate.of(2026, 7, 1)); r.setDateFin(LocalDate.of(2026, 7, 8));
        r.setStatut(StatutReservation.CONFIRMEE); r.setClient(new Client()); r.setVehicule(new Vehicule());
        return r;
    }
    @Override protected Reservation invalide() { Reservation r = valide(); r.setClient(null); return r; }
    @Override protected Reservation modifiee() {
        Reservation r = new Reservation();
        r.setDateDebut(LocalDate.of(2026, 8, 1)); r.setDateFin(LocalDate.of(2026, 8, 3));
        r.setStatut(StatutReservation.ANNULEE); r.setClient(new Client()); r.setVehicule(new Vehicule());
        return r;
    }
    @Override protected void verifieChamps(Reservation r, Reservation s) {
        assertEquals(s.getDateDebut(), r.getDateDebut());
        assertEquals(s.getDateFin(), r.getDateFin());
        assertEquals(s.getStatut(), r.getStatut());
        assertSame(s.getClient(), r.getClient());
        assertSame(s.getVehicule(), r.getVehicule());
    }

    @Test
    void check_dateFinAvantDateDebut_estInvalide() {
        Reservation r = valide();
        r.setDateFin(r.getDateDebut().minusDays(1));
        assertFalse(service.check(r, true));
    }

    @Test
    void check_statutAbsent_estInvalide() {
        Reservation r = valide();
        r.setStatut(null);
        assertFalse(service.check(r, true));
    }
}
