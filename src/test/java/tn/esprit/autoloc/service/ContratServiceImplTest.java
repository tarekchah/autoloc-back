package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.repository.IContratRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ContratServiceImplTest extends GenericServiceTestBase<Contrat, Long> {
    @Mock private IContratRepository repository;
    private ContratServiceImpl service;

    @BeforeEach
    void init() { service = new ContratServiceImpl(repository); }

    @Override protected GenericServiceImpl<Contrat, Long> service() { return service; }
    @Override protected CrudRepository<Contrat, Long> repository() { return repository; }
    @Override protected Long id() { return 1L; }
    @Override protected void setId(Contrat c, Long id) { c.setIdContrat(id); }

    @Override protected Contrat valide() {
        Contrat c = new Contrat();
        c.setDateSignature(LocalDate.of(2026, 2, 1)); c.setMontantTotal(new BigDecimal("500")); c.setValide(true);
        return c;
    }
    @Override protected Contrat invalide() { Contrat c = valide(); c.setDateSignature(null); return c; }
    @Override protected Contrat modifiee() {
        Contrat c = new Contrat();
        c.setDateSignature(LocalDate.of(2026, 5, 5)); c.setMontantTotal(new BigDecimal("900")); c.setValide(false);
        return c;
    }
    @Override protected void verifieChamps(Contrat r, Contrat s) {
        assertEquals(s.getDateSignature(), r.getDateSignature());
        assertEquals(s.getMontantTotal(), r.getMontantTotal());
        assertEquals(s.isValide(), r.isValide());
    }

    @Test
    void check_montantNulOuNegatif_estInvalide() {
        Contrat c = valide();
        c.setMontantTotal(BigDecimal.ZERO);
        assertFalse(service.check(c, true));
        c.setMontantTotal(new BigDecimal("-10"));
        assertFalse(service.check(c, true));
    }

    @Test
    void check_montantAbsent_estInvalide() {
        Contrat c = valide();
        c.setMontantTotal(null);
        assertFalse(service.check(c, true));
    }
}
