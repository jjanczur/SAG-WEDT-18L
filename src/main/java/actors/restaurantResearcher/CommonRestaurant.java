package actors.restaurantResearcher;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonRestaurant {
    public int zomato_id;
    public String google_id;
    public String origin;
    public String dailyMenu;
    public String name;
    public String address;

    public CommonRestaurant(int rid, String rorigin,
                            String dm, String rname,
                            String raddress) {

        zomato_id = rid;
        origin = rorigin;
        dailyMenu = dm;
        name = rname;
        address = raddress;
    };

    public CommonRestaurant(String rid, String rorigin,
                            String dm, String rname,
                            String raddress) {

        google_id = rid;
        origin = rorigin;
        dailyMenu = dm;
        name = rname;
        address = raddress;
    };


}
