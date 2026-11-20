package tn.esprit.autoloc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests communs à tous les services CRUD (GenericServiceImpl).
 * Chaque service de test fournit ses entités valides/invalides et son dépôt simulé (Mockito).
 */
@ExtendWith(MockitoExtension.class)
abstract class GenericServiceTestBase<T, K> {

    protected abstract GenericServiceImpl<T, K> service();
    protected abstract CrudRepository<T, K> repository();
    protected abstract K id();
    protected abstract void setId(T entite, K id);
    /** Entité valide, sans identifiant. */
    protected abstract T valide();
    /** Entité sans identifiant mais dont un champ obligatoire est manquant. */
    protected abstract T invalide();
    /** Entité valide aux valeurs différentes de valide(), pour tester update. */
    protected abstract T modifiee();
    /** Vérifie que les champs de la source ont été copiés dans le résultat. */
    protected abstract void verifieChamps(T resultat, T source);

    // ---------- create ----------
    @Test
    void create_entiteValide_estEnregistree() {
        T entite = valide();
        when(repository().save(entite)).thenReturn(entite);

        assertSame(entite, service().create(entite));
        verify(repository()).save(entite);
    }

    @Test
    void create_avecIdentifiant_estRefusee() {
        T entite = valide();
        setId(entite, id());

        GenericServiceImpl<T, K> service = service();

        assertThrows(IllegalArgumentException.class, () -> service.create(entite));
        verify(repository(), never()).save(any());
    }

    @Test
    void create_champObligatoireManquant_estRefusee() {
        T entite = invalide();
        GenericServiceImpl<T, K> service = service();

        assertThrows(IllegalArgumentException.class, () -> service.create(entite));
        verify(repository(), never()).save(any());
    }

    // ---------- lecture ----------
    @Test
    void findById_existant_retourneEntite() {
        T entite = valide();
        when(repository().findById(id())).thenReturn(Optional.of(entite));

        assertSame(entite, service().findById(id()));
    }

    @Test
    void findById_inconnu_leveResourceNotFound() {
        when(repository().findById(id())).thenReturn(Optional.empty());
        GenericServiceImpl<T, K> service = service();
        K id = id();

        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }

    @Test
    void findAll_retourneToutesLesEntites() {
        T a = valide();
        T b = modifiee();
        when(repository().findAll()).thenReturn(List.of(a, b));

        List<T> resultat = service().findAll();

        assertEquals(2, resultat.size());
        assertTrue(resultat.contains(a) && resultat.contains(b));
    }

    @Test
    void findAll_aucuneEntite_retourneListeVide() {
        when(repository().findAll()).thenReturn(List.of());

        assertTrue(service().findAll().isEmpty());
    }

    // ---------- update ----------
    @Test
    void update_existant_copieLesChampsEtEnregistre() {
        T existante = valide();
        T nouvelle = modifiee();
        when(repository().findById(id())).thenReturn(Optional.of(existante));
        when(repository().save(existante)).thenReturn(existante);

        T resultat = service().update(id(), nouvelle);

        assertSame(existante, resultat);
        verifieChamps(resultat, nouvelle);
        verify(repository()).save(existante);
    }

    @Test
    void update_inconnu_leveResourceNotFound() {
        when(repository().findById(id())).thenReturn(Optional.empty());
        T nouvelle = modifiee();
        GenericServiceImpl<T, K> service = service();
        K id = id();

        assertThrows(ResourceNotFoundException.class, () -> service.update(id, nouvelle));
        verify(repository(), never()).save(any());
    }

    @Test
    void update_champObligatoireManquant_estRefusee() {
        when(repository().findById(id())).thenReturn(Optional.of(valide()));
        T incorrecte = invalide();
        GenericServiceImpl<T, K> service = service();
        K id = id();

        assertThrows(IllegalArgumentException.class, () -> service.update(id, incorrecte));
        verify(repository(), never()).save(any());
    }

    // ---------- delete ----------
    @Test
    void deleteById_existant_supprime() {
        when(repository().existsById(id())).thenReturn(true);

        service().deleteById(id());

        verify(repository()).deleteById(id());
    }

    @Test
    void deleteById_inconnu_leveResourceNotFound() {
        when(repository().existsById(id())).thenReturn(false);
        GenericServiceImpl<T, K> service = service();
        K id = id();

        assertThrows(ResourceNotFoundException.class, () -> service.deleteById(id));
        verify(repository(), never()).deleteById(any());
    }
}
