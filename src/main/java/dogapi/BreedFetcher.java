package dogapi;

import java.util.List;

/**
 * Interface for the service of getting sub breeds of a given dog breed.
 */
public interface BreedFetcher {

    /**
     * Fetch the list of sub breeds for the given breed.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist
     */
    List<String> getSubBreeds(String breed) throws BreedNotFoundException;

    // Updated for Task 4: make this a checked exception (extends Exception, not RuntimeException)
    // A class defined in an interface is public AND static by default
    class BreedNotFoundException extends Exception {
        public BreedNotFoundException(String breed) {
            super("Breed not found: " + breed);
        }
    }
}