package actors.restaurantResearcher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.util.List;


public class FacebookCollection {
    private static final Logger log = Logger.getLogger(FacebookCollection.class);

    @SerializedName("data")
    @Expose
    private List<Data> data = null;
    @SerializedName("paging")
    @Expose
    private Paging paging;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }


}
