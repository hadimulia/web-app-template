package app.spring.web.model;

public class PageRequest {
    private int page = 1;
    private int size = 10;
    private String sortBy = "id";
    private String sortDir = "asc";
    private String search = "";

    public PageRequest() {}

    public PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public PageRequest(int page, int size, String sortBy, String sortDir) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDir = sortDir;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(1, page);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = Math.max(1, Math.min(100, size));
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy != null ? sortBy : "id";
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = "desc".equalsIgnoreCase(sortDir) ? "desc" : "asc";
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search != null ? search.trim() : "";
    }

    public int getOffset() {
        return (page - 1) * size;
    }

    public boolean hasSearch() {
        return search != null && !search.trim().isEmpty();
    }
}
