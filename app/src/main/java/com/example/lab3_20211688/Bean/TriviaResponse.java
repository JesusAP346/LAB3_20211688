package com.example.lab3_20211688.Bean;

import java.util.List;

public class TriviaResponse {
    private List<PreguntaBean> results;

    public List<PreguntaBean> getResults() {
        return results;
    }

    public void setResults(List<PreguntaBean> results) {
        this.results = results;
    }
}
