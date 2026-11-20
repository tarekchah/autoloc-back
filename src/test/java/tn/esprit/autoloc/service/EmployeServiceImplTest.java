package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.RoleEmploye;
import tn.esprit.autoloc.repository.IEmployeRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeServiceImplTest extends GenericServiceTestBase<Employe, Long> {
    @Mock private IEmployeRepository repository;
    private EmployeServiceImpl service;

    @BeforeEach
    void init() { service = new EmployeServiceImpl(repository); }

    @Override protected GenericServiceImpl<Employe, Long> service() { return service; }
    @Override protected CrudRepository<Employe, Long> repository() { return repository; }
    @Override protected Long id() { return 1L; }
    @Override protected void setId(Employe e, Long id) { e.setIdEmploye(id); }

    @Override protected Employe valide() {
        Employe e = new Employe(); e.setNom("Gharbi"); e.setPrenom("Omar"); e.setRole(RoleEmploye.AGENT); return e;
    }
    @Override protected Employe invalide() { Employe e = valide(); e.setRole(null); return e; }
    @Override protected Employe modifiee() {
        Employe e = new Employe(); e.setNom("Mansour"); e.setPrenom("Lina"); e.setRole(RoleEmploye.MANAGER); return e;
    }
    @Override protected void verifieChamps(Employe r, Employe s) {
        assertEquals(s.getNom(), r.getNom());
        assertEquals(s.getPrenom(), r.getPrenom());
        assertEquals(s.getRole(), r.getRole());
    }
}
