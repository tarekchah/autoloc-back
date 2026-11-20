package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.repository.IClientRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientServiceImplTest extends GenericServiceTestBase<Client, Long> {
    @Mock private IClientRepository repository;
    private ClientServiceImpl service;

    @BeforeEach
    void init() { service = new ClientServiceImpl(repository); }

    @Override protected GenericServiceImpl<Client, Long> service() { return service; }
    @Override protected CrudRepository<Client, Long> repository() { return repository; }
    @Override protected Long id() { return 1L; }
    @Override protected void setId(Client c, Long id) { c.setIdClient(id); }

    @Override protected Client valide() {
        Client c = new Client();
        c.setNom("Ben Ali"); c.setPrenom("Sami"); c.setEmail("sami@mail.tn");
        c.setTelephone("20123456"); c.setNumPermis("P-123"); c.setDateInscription(LocalDate.of(2026, 1, 10));
        return c;
    }
    @Override protected Client invalide() { Client c = valide(); c.setEmail(""); return c; }
    @Override protected Client modifiee() {
        Client c = valide();
        c.setNom("Trabelsi"); c.setPrenom("Aya"); c.setEmail("aya@mail.tn");
        c.setTelephone("99887766"); c.setNumPermis("P-999"); c.setDateInscription(LocalDate.of(2026, 3, 1));
        return c;
    }
    @Override protected void verifieChamps(Client r, Client s) {
        assertEquals(s.getNom(), r.getNom());
        assertEquals(s.getPrenom(), r.getPrenom());
        assertEquals(s.getEmail(), r.getEmail());
        assertEquals(s.getTelephone(), r.getTelephone());
        assertEquals(s.getNumPermis(), r.getNumPermis());
        assertEquals(s.getDateInscription(), r.getDateInscription());
    }
}
