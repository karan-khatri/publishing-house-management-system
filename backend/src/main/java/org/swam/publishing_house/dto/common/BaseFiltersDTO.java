package org.swam.publishing_house.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseFiltersDTO {
    private String query;
    private int page;
    private int limit;
    private String sortBy;
    private String sortDir;

    public BaseFiltersDTO() {}

    public BaseFiltersDTO(String query, int page, int limit, String sortBy, String sortDir) {
        this.query = query;
        this.page = page;
        this.limit = limit;
        this.sortBy = sortBy;
        this.sortDir = sortDir;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
}
