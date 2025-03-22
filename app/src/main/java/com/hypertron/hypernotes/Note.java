package com.hypertron.hypernotes;

public class Note {
    private long id;
    private String title;
    private String content;
    private long timestamp;
    private int colorResId;
    private int symbolIndex;
    private String customEmoji;

    // Constructor with colorResId
    public Note(long id, String title, String content, long timestamp, int colorResId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.colorResId = colorResId;
        this.symbolIndex = -1;
        this.customEmoji = null;
    }

    // Getter methods
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public int getColorResId() { return colorResId; }
    public int getSymbolIndex() { return symbolIndex; }
    public String getCustomEmoji() { return customEmoji; }
    
    // Setter methods
    public void setColorResId(int colorResId) { this.colorResId = colorResId; }
    public void setSymbolIndex(int symbolIndex) { this.symbolIndex = symbolIndex; }
    public void setCustomEmoji(String customEmoji) { this.customEmoji = customEmoji; }
}