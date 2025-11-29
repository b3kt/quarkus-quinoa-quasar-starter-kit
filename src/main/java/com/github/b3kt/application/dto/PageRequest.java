package com.github.b3kt.application.dto;

/**
 * DTO for pagination request parameters.
 */
public class PageRequest {
    private int page = 1;
    private int rowsPerPage = 10;
    private String sortBy;
    private boolean descending = false;
    private String search;
    private String statusFilter;

    public PageRequest() {
    }

    public PageRequest(int page, int rowsPerPage) {
        this.page = page;
        this.rowsPerPage = rowsPerPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter(String statusFilter) {
        this.statusFilter = statusFilter;
    }

    private boolean filterToday = false;

    public boolean isFilterToday() {
        return filterToday;
    }

    public void setFilterToday(boolean filterToday) {
        this.filterToday = filterToday;
    }

    public int getOffset() {
        return (page - 1) * rowsPerPage;
    }
}
