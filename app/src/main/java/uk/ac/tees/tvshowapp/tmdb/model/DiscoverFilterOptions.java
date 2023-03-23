package uk.ac.tees.tvshowapp.tmdb.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiscoverFilterOptions implements Serializable {
    private SortOption sortBy;
    private boolean sortDescending = true;
    private Date startDate;
    private Date endDate;
    private double minVoteAverage;
    private int minVotes = 20;
    private ArrayList<Integer> genreIds;

    public SortOption getSortBy() {
        return sortBy;
    }

    public String getSortString() {
        if (sortBy != null) {
            return sortBy.name() + "." + (sortDescending ? "desc" : "asc");
        }
        return null;
    }

    public void setSortBy(SortOption sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isSortDescending() {
        return sortDescending;
    }

    public void setSortDescending(boolean sortDescending) {
        this.sortDescending = sortDescending;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getStartDateString() {
        if(startDate != null){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = dateFormat.format(startDate);
            return date;
        }
        return null;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getEndDateString() {
        if(endDate != null){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = dateFormat.format(endDate);
            return date;
        }
        return null;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getMinVoteAverage() {
        return minVoteAverage;
    }

    public void setMinVoteAverage(double minVoteAverage) {
        this.minVoteAverage = minVoteAverage;
    }

    public int getMinVotes() {
        return minVotes;
    }

    public void setMinVotes(int minVotes) {
        this.minVotes = minVotes;
    }

    public ArrayList<Integer> getGenreIds() {
        return genreIds;
    }

    // formatted for discover request;
    public String getGenresString() {
        if(genreIds != null){
            String out = "";
            for (Integer id : genreIds) {
                out += id + ",";
            }
            return out;
        }
        return null;
    }

    public void setGenreIds(ArrayList<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    // needs .asc or .desc suffix
    public enum SortOption {
        popularity("Popularity"),
        vote_average("Rating"),
        first_air_date("Air Date");

        String name;

        SortOption(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
