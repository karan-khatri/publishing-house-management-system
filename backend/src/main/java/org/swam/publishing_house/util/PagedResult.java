package org.swam.publishing_house.util;

import java.util.List;

public class PagedResult<T> {
    private List<T> content;
    private int page;
    private int limit;
    private long totalItems;

    public PagedResult() {}

    public PagedResult(List<T> content, int page, int limit, long totalItems) {
        this.content = content;
        this.page = page;
        this.limit = limit;
        this.totalItems = totalItems;
    }

    // Getters and setters
    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }

    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }
}