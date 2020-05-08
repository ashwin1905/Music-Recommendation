package com.zhijie.meditation.focus;

class Music {
    private String name;
    private String singer;
    private int song;

    public Music(String name, int song) {
        this.name = name;
        //this.singer = singer;
        this.song = song;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getSong() {
        return song;
    }

    public void setSong(int song) {
        this.song = song;
    }
}
