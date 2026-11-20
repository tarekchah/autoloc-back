package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.repository.IEquipementRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EquipementServiceImplTest extends GenericServiceTestBase<Equipement, Long> {
    @Mock private IEquipementRepository repository;
    private EquipementServiceImpl service;

    @BeforeEach
    void init() { service = new EquipementServiceImpl(repository); }

    @Override protected GenericServiceImpl<Equipement, Long> service() { return service; }
    @Override protected CrudRepository<Equipement, Long> repository() { return repository; }
    @Override protected Long id() { return 1L; }
    @Override protected void setId(Equipement e, Long id) { e.setIdEquipement(id); }

    @Override protected Equipement valide() { Equipement e = new Equipement(); e.setLibelle("GPS"); return e; }
    @Override protected Equipement invalide() { Equipement e = new Equipement(); e.setLibelle(""); return e; }
    @Override protected Equipement modifiee() { Equipement e = new Equipement(); e.setLibelle("Siège bébé"); return e; }
    @Override protected void verifieChamps(Equipement r, Equipement s) { assertEquals(s.getLibelle(), r.getLibelle()); }
}
