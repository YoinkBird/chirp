package io.chirp.messenger;
public class LocationState {
  private int punctuality = 0;
  private static String[] friendly = new String[]{
            "not going to make it",
            "good trajectory",
            "already within office GPS radius",
    };
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

  // simple check: in office, or not in office?
  public boolean is_punctual(){
    if(this.get_punctuality() == 2){
      return true;
    } else {
      return false;
    }
  }

  // slightly more information than is_punctual
  // 0 : not going to make it
  // 1 : good trajectory
  // 2 : already within office GPS radius
  public int get_punctuality(){
    // return code
    int rc = 0;
    // server request
    // rc = blah blah blah
    // mock request
    rc = this.mock_calculate_dist();
    return rc;
  }

  // MOCK: hard-code
  public int mock_calculate_dist(){
    // return code
    int rc = 0;
    // tolerance - "on-time" range
    float tolerance = 0.003f;
    // boundary - border of office
    float boundary  = 0.001f;
    // lat+long of office
    float office_lat=30.2758021f;
    float office_lon=-97.7331734f;
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
    // make sure values are up-to-date
    this.get_punctuality();

    return friendly[this.punctuality];
  }
}
/*
	<wpt lat="30.2758021" lon="-97.7331734">
 */
