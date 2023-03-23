package uk.ac.tees.tvshowapp.tmdb.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CastExtrernalId {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("twitter_id")
    @Expose
    private String twitterId;
    @SerializedName("facebook_id")
    @Expose
    private String facebookId;
    @SerializedName("tvrage_id")
    @Expose
    private Integer tvrageId;
    @SerializedName("instagram_id")
    @Expose
    private Object instagramId;
    @SerializedName("freebase_mid")
    @Expose
    private String freebaseMid;
    @SerializedName("imdb_id")
    @Expose
    private String imdbId;
    @SerializedName("freebase_id")
    @Expose
    private String freebaseId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public Integer getTvrageId() {
        return tvrageId;
    }

    public void setTvrageId(Integer tvrageId) {
        this.tvrageId = tvrageId;
    }

    public Object getInstagramId() {
        return instagramId;
    }

    public void setInstagramId(Object instagramId) {
        this.instagramId = instagramId;
    }

    public String getFreebaseMid() {
        return freebaseMid;
    }

    public void setFreebaseMid(String freebaseMid) {
        this.freebaseMid = freebaseMid;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getFreebaseId() {
        return freebaseId;
    }

    public void setFreebaseId(String freebaseId) {
        this.freebaseId = freebaseId;
    }

}
