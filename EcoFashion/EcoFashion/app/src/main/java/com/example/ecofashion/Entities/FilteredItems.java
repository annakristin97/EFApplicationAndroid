package com.example.ecofashion.Entities;

import java.util.List;

public class FilteredItems {

    private List<Item> Items;

    public FilteredItems() {

    }

    public FilteredItems(List<Item> items) { this.Items = items; }

    public List<Item> getItems() {
        return Items;
    }

    public void setItems(List<Item> items) {
        this.Items = items;
    }
}

