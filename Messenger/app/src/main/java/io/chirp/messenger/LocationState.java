package io.chirp.messenger;
public class LocationState {
  private int punctuality = 0;
  private float gps_lat;
  private float gps_long;
  public LocationState(){
  }
  public LocationState(float lastLat, float lastLong){
    this.update_coord(lastLat, lastLong);
  }
  public void update_coord(float lastLat, float lastLong){
    this.gps_lat = lastLat;
    this.gps_long = lastLong;
  }

  // 0 : not going to make it
  // 1 : good trajectory
  // 2 : already within office GPS radius
  public int is_punctual(){
    // return code
    int rc = 0;
    // MOCK: hard-code
    // tolerance - "on-time" range
    int tolerance = 5;
    // boundary - border of office
    int boundary  = 1;
    // lat+long of office
    int office_lat  = 50;
    int office_long = 50;
    // 0 : not going to make it - out of tolerance
    if(this.gps_lat < ( office_lat - tolerance) && 
       this.gps_lat > ( office_lat + tolerance) ){
      rc = 0;
    }
    // 1 : good trajectory - within tolerance
    if(this.gps_lat > ( office_lat - tolerance) && 
       this.gps_lat < ( office_lat + tolerance) ){
      rc = 1;
    }
    // 2 : already within office range
    if(this.gps_lat >= ( office_lat - boundary) && 
       this.gps_lat <= ( office_lat + boundary) ){
      rc = 2;
    }
    this.punctuality = rc;
    return this.punctuality;
  }

  public String is_punctual_friendly(){
    this.is_punctual();

    String[] friendly = new String[]{
            "not going to make it",
            "good trajectory",
            "already within office GPS radius",
    };
    return friendly[this.punctuality];
  }
}
