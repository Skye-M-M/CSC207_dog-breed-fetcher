package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        if (breed == null || breed.trim().isEmpty()) {
            throw new BreedNotFoundException(breed);
        }

        String url = "https://dog.ceo/api/breed/" + breed.toLowerCase(Locale.ROOT) + "/list";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new BreedNotFoundException(breed);
            }

            String body = response.body().string();
            JSONObject json = new JSONObject(body);

            String status = json.optString("status", "error");
            if (!status.equalsIgnoreCase("success")) {
                // Covers both "error" responses and other unexpected statuses
                throw new BreedNotFoundException(breed);
            }

            JSONArray arr = json.optJSONArray("message");
            List<String> subBreeds = new ArrayList<>();
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    subBreeds.add(arr.getString(i));
                }
            }

            return subBreeds;

        } catch (IOException | org.json.JSONException e) {
            // Any failure maps to BreedNotFoundException per spec
            throw new BreedNotFoundException(breed);
        }
    }
}