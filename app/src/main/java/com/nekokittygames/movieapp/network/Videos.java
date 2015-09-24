
package com.nekokittygames.movieapp.network;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class Videos {

    @Expose
    private List<Result_> results = new ArrayList<Result_>();

    /**
     * 
     * @return
     *     The results
     */
    public List<Result_> getResults() {
        return results;
    }

    /**
     * 
     * @param results
     *     The results
     */
    public void setResults(List<Result_> results) {
        this.results = results;
    }

}
