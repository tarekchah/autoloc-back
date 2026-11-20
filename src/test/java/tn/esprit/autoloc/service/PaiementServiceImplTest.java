package tn.esprit.autoloc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IPaiementRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaiementServiceImplTest {
    @Mock private IPaiementRepository repository;
    @InjectMocks private PaiementServiceImpl service;

    @Test
    void findById_existant_retournePaiement() {
        Paiement p = new Paiement();
        when(repository.findById(1L)).thenReturn(Optional.of(p));

        assertSame(p, service.findById(1L));
    }

    @Test
    void findById_inconnu_leveResourceNotFound() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(9L));
    }

    @Test
    void findAll_retourneTousLesPaiements() {
        when(repository.findAll()).thenReturn(List.of(new Paiement(), new Paiement()));

        assertEquals(2, service.findAll().size());
    }
}
