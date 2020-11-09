package com.example.view;

import java.io.Serializable;

/**
 * Created by Tinglan on 2020/10/21 19:05
 * It works!!
 */
public class StarInfo implements Serializable {
    int color;
    boolean visited;
    boolean removed;

    public StarInfo(int color, boolean visited, boolean removed) {
        this.color = color;
        this.visited = visited;
        this.removed = removed;
    }

    public StarInfo(StarInfo starInfo){
        this.color = starInfo.color;
        this.visited = starInfo.visited;
        this.removed = starInfo.removed;
    }
}
