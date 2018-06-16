package com.nuno1212s.mercado.searchmanager;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
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
        /*
         * Can not select multiple search parameters of the same category
          * (Can't select a diamond pick and a diamond axe for example, because that is impossible)
         */
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
