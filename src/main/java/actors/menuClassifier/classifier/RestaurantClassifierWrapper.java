package actors.menuClassifier.classifier;

import actors.restaurantResearcher.CommonRestaurant;
import edu.stanford.nlp.stats.Counter;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@Setter
public class RestaurantClassifierWrapper {
    private CommonRestaurant restaurant;
    private Map<String, Double> tfIdfs;
    private List<String> tokensAfterLemma;
    private Counter<String> termFrequencies;
    private Map<List<String>, double[]> vecs;
    private Map<List<String>, Double> cosineSimilarities;

    public RestaurantClassifierWrapper(CommonRestaurant restaurant) {
        this.restaurant = restaurant;
        this.vecs = new HashMap<>();
        this.cosineSimilarities = new HashMap<>();
    }

}
