package tn.esprit.autoloc;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.service.IAgenceService;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@Ignore("Test d'un ancien atelier : désactivé (écrit dans la vraie base)")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AgenceTests {
    @Autowired
    private AgenceRepositoryMock basicAgenceRepository;

    @Autowired
    private IAgenceRepository fullAgenceRepository;

    @Autowired
    private IAgenceService agenceService;


    private Agence addAgence(CrudRepository<Agence , Long> repository){

        int valeur = (int) System.currentTimeMillis();

        Agence a = new Agence();
        a.setNom("Agence ariana");
        a.setAdresse("1 Rue Hedi");
        a.setVille("Tunis");
        a.setTelephone("71585874");

        Vehicule v1 = new Vehicule();
        v1.setCategorie(CategorieVehicule.SUV);
        v1.setImmatriculation("785414TU96" + valeur);
        v1.setMarque("Isuzu");
        v1.setModele("DMax");
        v1.setStatut(StatutVehicule.EN_MAINTENANCE);
        v1.setTarifJournalier(new BigDecimal(100));

        Vehicule v2 = new Vehicule();
        v2.setCategorie(CategorieVehicule.UTILITAIRE);
        v2.setImmatriculation("785414TU95" + valeur);
        v2.setMarque("Toyota");
        v2.setModele("Yaris");
        v2.setStatut(StatutVehicule.DISPONIBLE);
        v2.setTarifJournalier(new BigDecimal(80));

        a.setVehicules(Set.of(v1,v2));
        return repository.save(a);
    }

    private String loadAgence(CrudRepository repository, String typeDepot) {
        Iterable<Agence> agences = repository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("Type de dépôt utilisé : ")
                .append(typeDepot)
                .append("\n");

        for (Object obj : agences) {
            Agence agence = (Agence) obj;

            sb.append(agence.getIdAgence())
                    .append(" | ")
                    .append(agence.getNom())
                    .append("\n");

            Set<Vehicule> vehicules = agence.getVehicules();
            int nb = (vehicules == null) ? 0 : vehicules.size();

            sb.append("Nombre de véhicules : ")
                    .append(nb)
                    .append("\n");

            if (vehicules != null) {
                for (Vehicule v : vehicules) {
                    sb.append("   ")
                            .append(v.getIdVehicule())
                            .append(" | ")
                            .append(v.getImmatriculation())
                            .append("\n");
                }
            }
        }

        System.out.println(sb);
        return sb.toString();
    }

    @Test
    public void basicAddAgence() {
        Agence creee = addAgence(basicAgenceRepository);
        assertNotNull("L'agence doit être enregistrée", creee);
        assertEquals("Agence ariana", creee.getNom());
    }

    @Test
    public void fullAddAgence() {
        Agence creee = addAgence(fullAgenceRepository);
        assertNotNull("L'identifiant doit être généré", creee.getIdAgence());
        assertEquals("Agence ariana", creee.getNom());
    }


    @Test
    public void basicLoadAgence() {
        String rapport = loadAgence(basicAgenceRepository, "Basic Repository Mock");
        assertTrue("Le rapport doit indiquer le dépôt utilisé", rapport.startsWith("Type de dépôt utilisé : Basic Repository Mock"));
    }

    @Test
    public void fullLoadAgence() {
        String rapport = loadAgence(fullAgenceRepository, "Full Repository");
        assertTrue("Le rapport doit indiquer le dépôt utilisé", rapport.startsWith("Type de dépôt utilisé : Full Repository"));
    }


    @Test
    public void loadSortedAgences() {
        Iterable<Agence> agences =
                fullAgenceRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "idAgence")
                );

        Long precedent = null;
        for (Agence agence : agences) {
            System.out.println("ID : " + agence.getIdAgence());
            System.out.println("Nom : " + agence.getNom());
            System.out.println("Adresse : " + agence.getAdresse());
            System.out.println("Ville : " + agence.getVille());
            System.out.println("Téléphone : " + agence.getTelephone());
            System.out.println("-------------------------");
            if (precedent != null) {
                assertTrue("Les agences doivent être triées par id décroissant", precedent > agence.getIdAgence());
            }
            precedent = agence.getIdAgence();
        }
    }

    @Test
    public void loadPagedAgences() {

        int taillePage = 2;

        Pageable pageable = PageRequest.of(
                0,
                taillePage,
                Sort.by(Sort.Direction.DESC, "idAgence")
        );

        Page<Agence> page = fullAgenceRepository.findAll(pageable);

        int totalPages = page.getTotalPages();

        System.out.println("Nombre total de pages : " + totalPages);

        for (int i = 0; i < totalPages; i++) {

            pageable = PageRequest.of(
                    i,
                    taillePage,
                    Sort.by(Sort.Direction.DESC, "idAgence")
            );

            page = fullAgenceRepository.findAll(pageable);
            assertTrue("Une page ne dépasse pas sa taille", page.getContent().size() <= taillePage);

            System.out.println("\n========== Page " + (i + 1) + " ==========");

            for (Agence agence : page.getContent()) {
                System.out.println("ID : " + agence.getIdAgence());
                System.out.println("Nom : " + agence.getNom());
                System.out.println("Adresse : " + agence.getAdresse());
                System.out.println("Ville : " + agence.getVille());
                System.out.println("Téléphone : " + agence.getTelephone());
                System.out.println("-------------------------");
            }
        }
    }

    @Test
    public void createAgence() {

        Agence a = new Agence();
        a.setNom("Agence Lac");
        a.setAdresse("5 Rue du Lac");
        a.setVille("Tunis");
        a.setTelephone("71123456");


        Agence creee = agenceService.create(a);

        // 3. Vérifie le résultat
        assertNotNull("L'identifiant doit être généré", creee.getIdAgence());
        assertEquals("Agence Lac", creee.getNom());
        assertEquals("71123456", creee.getTelephone());

        System.out.println("Agence créée avec l'ID : " + creee.getIdAgence());
    }
}

@Repository
interface AgenceRepositoryMock extends CrudRepository<Agence, Long>{

}