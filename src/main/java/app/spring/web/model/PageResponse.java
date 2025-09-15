package app.spring.web.model;

import java.util.List;

public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private String sortBy;
    private String sortDir;
    private String search;

    public PageResponse() {}

    public PageResponse(List<T> content, PageRequest pageRequest, long totalElements) {
        this.content = content;
        this.page = pageRequest.getPage();
        this.size = pageRequest.getSize();
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.first = page == 1;
        this.last = page >= totalPages;
        this.sortBy = pageRequest.getSortBy();
        this.sortDir = pageRequest.getSortDir();
        this.search = pageRequest.getSearch();
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public boolean hasNext() {
        return !last;
    }

    public boolean hasPrevious() {
        return !first;
    }

    public int getNextPage() {
        return hasNext() ? page + 1 : page;
    }

    public int getPreviousPage() {
        return hasPrevious() ? page - 1 : page;
    }
}
