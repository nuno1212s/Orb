package com.nuno1212s.mercado.searchmanager;

import com.nuno1212s.mercado.marketmanager.Item;

/**
 * Search parameters
 */
public abstract class SearchParameter {

    public abstract boolean fitsSearch(Item item);

}
