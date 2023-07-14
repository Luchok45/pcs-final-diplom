class PageEntry {
    private String fileName;
    private int page;
    private String text;
    private int count;

    public PageEntry(String fileName, int pageNumber, String text) {
        this.fileName = fileName;
        this.page = pageNumber;
        if (text != null) {
            this.text = text;
        } else {
            throw new IllegalArgumentException("Invalid text value");
        }
    }
    public PageEntry(String fileName, int pageNumber, int count) {
        this.fileName = fileName;
        this.page = pageNumber;
        this.count = count;
    }

    public String getFileName() {
        return fileName;
    }
    public int getPage() {
        return page;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "File: " + fileName + " | Page: " + page + " | Word count: " + count;
    }
}
