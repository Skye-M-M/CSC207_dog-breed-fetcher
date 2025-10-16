package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher delegate;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.delegate = Objects.requireNonNull(fetcher, "delegate fetcher must not be null");
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // Normalize the cache key so "Hound" and "hound" hit the same entry.
        String key = (breed == null) ? null : breed.toLowerCase(Locale.ROOT);

        // Serve from cache if present.
        if (key != null && cache.containsKey(key)) {
            // Return a copy to prevent external mutation of our cached list.
            return new ArrayList<>(cache.get(key));
        }

        // Miss: call the underlying fetcher and record that a call was made.
        callsMade++;
        try {
            List<String> result = delegate.getSubBreeds(breed);

            // Cache successful results (including empty lists).
            if (key != null) {
                // Store a defensive copy and also return a fresh copy.
                cache.put(key, new ArrayList<>(result));
                return new ArrayList<>(result);
            }
            // If key is null, just return (delegate decides validity).
            return result;
        } catch (BreedNotFoundException e) {
            // Do NOT cache failures; just propagate.
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}