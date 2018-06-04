package actors.restaurantResearcher;


import java.io.Serializable;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CommonRestaurant implements Serializable {
    private static final long serialVersionUID = 3909290786117608273L;
    public int zomato_id;
    public String google_id;
    public String origin;
    public String dailyMenu;
    public String name;
    public String address;
    public String url;

    public CommonRestaurant(int rid, String rorigin,
                            String dm, String rname,
                            String raddress, String rurl) {

        zomato_id = rid;
        origin = rorigin;
        dailyMenu = dm;
        name = rname;
        address = raddress;
    }

    public CommonRestaurant(String rid, String rorigin,
                            String dm, String rname,
                            String raddress, String rurl) {

        google_id = rid;
        origin = rorigin;
        dailyMenu = dm;
        name = rname;
        address = raddress;
    }

    @Override
    public String toString() {
        return "CommonRestaurant{" +
                ",\n name='" + name + '\'' +
                ",\n address='" + address + '\'' +
                "\n dailyMenu='" + dailyMenu + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        int result = zomato_id;
        result = 31 * result + (google_id != null ? google_id.hashCode() : 0);
        result = 31 * result + (origin != null ? origin.hashCode() : 0);
        result = 31 * result + (dailyMenu != null ? dailyMenu.hashCode() : 0);
        result = 31 * result + name.hashCode();
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        if(obj instanceof CommonRestaurant) {
            CommonRestaurant comp = (CommonRestaurant) obj;
            return this.name.equals(comp.name);
        }
        return false;
    }

}
