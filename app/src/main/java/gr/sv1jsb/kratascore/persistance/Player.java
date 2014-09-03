package gr.sv1jsb.kratascore.persistance;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by andreas on 11/8/2014.
 */
public class Player implements Parcelable{
    public long _id;
    public String player;
    public String photo;
    public String fb_id;

    private static int _ID = 0;
    private static int PLAYER = KrataScoreContract.PlayerEntry.NUM_PLAYER;
    private static int PHOTO = KrataScoreContract.PlayerEntry.NUM_PHOTO;
    private static int FB_ID = KrataScoreContract.PlayerEntry.NUM_FB_ID;

    public Player(long id, String player, String photo, String fb_id){
        this._id = id;
        this.player = player;
        this.photo = photo;
        this.fb_id = fb_id;
    }

    public static Player newInstanceFromCursor(Cursor c){
        return new Player(c.getLong(_ID), c.getString(PLAYER), c.getString(PHOTO), c.getString(FB_ID));
    }

    public Player(){}

    public String toString(){
        return "_id: " + String.valueOf(_id) + " player: " + player + " photo: " + photo + " fb_id: " + fb_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(_id);
        parcel.writeString(player);
        parcel.writeString(photo);
        parcel.writeString(fb_id);
    }

    public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
    private Player(Parcel in) {
        _id = in.readLong();
        player = in.readString();
        photo = in.readString();
        fb_id = in.readString();
    }
}
