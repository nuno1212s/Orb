package com.nuno1212s.mercado.searchmanager;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds search parameters
 */
public class SearchParameterBuilder {

    @Getter
    private List<SearchParameter> searchParameters;

    private SearchParameterBuilder() {
        searchParameters = new ArrayList<>();
    }

    public SearchParameterBuilder addSearchParameter(SearchParameter parameter) {
        searchParameters.add(parameter);
        return this;
    }

    public SearchParameterBuilder removeSearchParameter(SearchParameter parameter) {
        searchParameters.remove(parameter);
        return this;
    }

    public boolean containsParameter(SearchParameter p) {
        return this.searchParameters.contains(p);
    }

    public SearchParameter[] build() {
        return searchParameters.toArray(new SearchParameter[searchParameters.size()]);
    }

    public static SearchParameterBuilder builder() {
        return new SearchParameterBuilder();
    }

}
