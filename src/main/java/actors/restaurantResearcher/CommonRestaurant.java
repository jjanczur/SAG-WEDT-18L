package actors.restaurantResearcher;

import java.net.URL;

public class CommonRestaurant {
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
    };
    public CommonRestaurant(String rid, String rorigin,
                            String dm, String rname,
                            String raddress, String rurl) {

        google_id = rid;
        origin = rorigin;
        dailyMenu = dm;
        name = rname;
        address = raddress;
    };

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
