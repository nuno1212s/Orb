package com.nuno1212s.mercado.searchmanager.searchparameters;

import com.nuno1212s.mercado.searchmanager.SearchParameter;

import java.lang.reflect.InvocationTargetException;

/**
 * Search parameters
 */
public enum SearchParameters {

    ITEM(ItemSearchParameter.class),
    ENCHANT(EnchantmentSearchParameter.class),
    CURRENCY(CurrencySearchParameter.class);

    Class<? extends SearchParameter> pClass;

    SearchParameters(Class<? extends SearchParameter> classes) {
        this.pClass = classes;
    }

    public SearchParameter instantiate(String name, String args) {
        try {
            return pClass.getConstructor(String.class, String.class).newInstance(name, args);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

}
